package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.ask.dto.AskResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NlToSqlService {

    private static final String SQL_SYSTEM_PROMPT_TEMPLATE = """
            You translate user questions into %s SQL queries for a competence testing platform.
            Questions may be asked in Estonian or English.

            SCHEMA:
            "user"(id SERIAL PK, email VARCHAR UNIQUE, role_id INT FK->role.id, status CHAR(1), created_at TIMESTAMP, updated_at TIMESTAMP)
            role(id SERIAL PK, name VARCHAR UNIQUE)
            profile(id SERIAL PK, user_id INT FK->"user".id, first_name VARCHAR, last_name VARCHAR, phone_number VARCHAR, created_at TIMESTAMP, updated_at TIMESTAMP)
            "group"(id SERIAL PK, name VARCHAR UNIQUE, status CHAR(1), created_at TIMESTAMP, updated_at TIMESTAMP)
            group_member(id SERIAL PK, group_id INT FK->"group".id, user_id INT FK->"user".id, added_by INT FK->"user".id, created_at TIMESTAMP, updated_at TIMESTAMP)

            NOTES:
            - Table names "user" and "group" are reserved words and MUST always be written in double quotes.
            - status values: 'A' = active, 'I' = inactive (deleted users have status 'I').
            - role.name values: 'ADMIN', 'HALDUR', 'KASUTAJA'.
            - A user's first and last name are in the profile table, not in "user".

            RULES:
           
            2. If you cannot answer from this schema, return: CANNOT_ANSWER
            3. Never generate INSERT, UPDATE, DELETE, DROP or any non-SELECT statement.
            4. Use only %s SQL syntax and functions.
            """;

    private static final String SUMMARY_SYSTEM_PROMPT = """
            You are a helpful assistant that summarizes data clearly.""";

    private static final String SUMMARY_USER_PROMPT_TEMPLATE = """
            Summarize this database query result in 1-3 plain sentences.
            Don't mention SQL or technical terms. Be specific about numbers.
            
            Question: %s
            Results (%d rows): %s
            """;

    private static final String SQL_USER_PROMPT_TEMPLATE = """
            Generate SQL syntax for the question below

            %s
            """;

    private final JdbcTemplate jdbcTemplate;
    private final ChatClient chatClient;
    private final String sqlSystemPrompt;

    public NlToSqlService(JdbcTemplate jdbcTemplate, ChatClient.Builder builder,
                          @Value("${nlsql.dialect}") String dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.chatClient = builder.build();
        this.sqlSystemPrompt = SQL_SYSTEM_PROMPT_TEMPLATE.formatted(dialect, dialect);
    }

    public AskResponse ask(String userQuestion) {
        validateInput(userQuestion);
        String generatedSql = generateSql(userQuestion);
        List<Map<String, Object>> databaseResults = runQuery(generatedSql);

        return generateResponse(userQuestion, databaseResults);
    }

    private void validateInput(String userQuestion) {
        if (userQuestion == null || userQuestion.isBlank()) {
            throw new IllegalArgumentException("Input text missing");
        }
    }

    private String generateSql(String userQuestion) {
        String generatedSql = callLlm(sqlSystemPrompt, SQL_USER_PROMPT_TEMPLATE.formatted(userQuestion)).answer();
        validateSqlQuery(generatedSql);

        return generatedSql;
    }

    private void validateSqlQuery(String generatedSql) {
        String upperCaseSql = generatedSql.toUpperCase();

        if (!upperCaseSql.startsWith("SELECT")) {
            throw new IllegalArgumentException("Only SELECT queries are allowed.");
        }

        if (upperCaseSql.matches(".*\\b(DROP|DELETE|INSERT|UPDATE|TRUNCATE|ALTER|GRANT|COPY|CALL|DO)\\b.*")) {
            throw new IllegalArgumentException("Query contains forbidden SQL keywords");
        }
    }

    private List<Map<String, Object>> runQuery(String generatedSql) {
        try {
            return jdbcTemplate.queryForList(generatedSql);
        } catch (BadSqlGrammarException exception) {
            throw new IllegalArgumentException("Could not answer the question - AI generated an invalid SQL query");
        }
    }

    private AskResponse generateResponse(String userQuestion, List<Map<String, Object>> databaseResults) {
        String answer = formatResult(userQuestion, databaseResults).answer();

        return AskResponse.builder()
                .answer(answer)
                .build();
    }

    private AskResponse formatResult(String userQuestion, List<Map<String, Object>> databaseResults) {
        String userPrompt = SUMMARY_USER_PROMPT_TEMPLATE.formatted(
                userQuestion,
                databaseResults.size(),
                databaseResults);

        return callLlm(SUMMARY_SYSTEM_PROMPT, userPrompt);
    }

    private AskResponse callLlm(String systemPrompt, String userPrompt) {
        AskResponse response = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .responseEntity(AskResponse.class)
                .getEntity();

        if (response == null) {
            throw new IllegalStateException("AI model returned an empty response");
        }

        return response;
    }
}