package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.aiquestion.dto.AiQuestionSaveRequest;
import ee.testiplatvorm.controller.aiquestion.dto.AskRequest;
import ee.testiplatvorm.controller.aiquestion.dto.GeneratedAnswerDto;
import ee.testiplatvorm.controller.aiquestion.dto.GeneratedQuestionDto;
import ee.testiplatvorm.infrastructure.exception.ForbiddenException;
import ee.testiplatvorm.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.testiplatvorm.persistence.QuestionType;
import ee.testiplatvorm.persistence.QuestionTypeRepository;
import ee.testiplatvorm.persistence.aiquestion.AiQuestion;
import ee.testiplatvorm.persistence.aiquestion.AiQuestionRepository;
import ee.testiplatvorm.persistence.aiquestionanswer.AiQuestionAnswer;
import ee.testiplatvorm.persistence.aiquestionanswer.AiQuestionAnswerRepository;
import ee.testiplatvorm.persistence.competencelevel.CompetenceLevel;
import ee.testiplatvorm.persistence.competencelevel.CompetenceLevelRepository;
import ee.testiplatvorm.persistence.question.Question;
import ee.testiplatvorm.persistence.question.QuestionRepository;
import ee.testiplatvorm.persistence.user.User;
import ee.testiplatvorm.persistence.user.UserRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static ee.testiplatvorm.Error.NO_PERMISSION_TO_CREATE_QUESTIONS;
import static ee.testiplatvorm.Status.STATUS_ACTIVE;
import static ee.testiplatvorm.Status.STATUS_PENDING;

@Service
public class AiQuestionService {

    private static final String SELECTION_SYSTEM_PROMPT = """
            You help a question author on a competence testing platform.
            From the author's free-text request, pick the competence level and question type
            the new test question should be created for.

            RULES:
            1. Choose competenceLevelId ONLY from the AVAILABLE COMPETENCE LEVELS list.
            2. Choose questionTypeId ONLY from the AVAILABLE QUESTION TYPES list.
            3. The author may write in Estonian or English and may use synonyms
               (e.g. "algaja" = beginner, "JS" = JavaScript, "valikvastustega" = choice question).
            4. If the question type is not mentioned, choose SINGLE_CHOICE.
            5. If the competence or the level cannot be determined with confidence, or the request matches
               several competence levels equally, set competenceLevelId and questionTypeId to null and write
               a short clarifyingQuestion in Estonian that names the available options.
            6. If everything is clear, set clarifyingQuestion to null.
            """;

    private static final String SELECTION_USER_PROMPT_TEMPLATE = """
            AVAILABLE COMPETENCE LEVELS:
            %s

            AVAILABLE QUESTION TYPES:
            %s

            Author's request:
            %s
            """;

    private static final String QUESTION_SYSTEM_PROMPT = """
            You are a test question generator for a competence testing platform.
            You write one new test question with its answer options.

            QUESTION TYPES:
            - SINGLE_CHOICE: 3-4 answers, exactly ONE answer has isCorrect = true.
            - MULTIPLE_CHOICE: 4-5 answers, at least ONE answer has isCorrect = true.
            - TRUE_FALSE: exactly 2 answers "Tõene" and "Väär", exactly ONE has isCorrect = true.

            RULES:
            1. Write the question and answers in Estonian.
            2. The question must match the given competence and level difficulty.
            3. Do not repeat or rephrase any of the existing questions.
            4. title is the question itself, max 100 characters.
            5. description is a short instruction for the test taker, max 1000 characters (e.g. "Vali õige vastus.").
            6. Each answerText is max 255 characters.
            7. Wrong answers must be plausible, but clearly incorrect to an expert.
            """;

    private static final String QUESTION_USER_PROMPT_TEMPLATE = """
            Generate one %s question.

            Competence: %s
            Competence description: %s
            Level: %s (%s)

            Existing questions for this competence level:
            %s

            Author's request (may also contain topic wishes):
            %s
            """;

    private static final String DEFAULT_CLARIFYING_QUESTION = "Millise kompetentsi ja taseme jaoks küsimus luua?";

    // AI vastuste kujud - neid ei tagastata otse frontendile
    public record QuestionTarget(Integer competenceLevelId, Integer questionTypeId, String clarifyingQuestion) {
    }

    public record AiGeneratedQuestion(String title, String description, List<GeneratedAnswerDto> answers) {
    }

    private final ChatClient chatClient;
    private final CompetenceLevelRepository competenceLevelRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final QuestionRepository questionRepository;
    private final AiQuestionRepository aiQuestionRepository;
    private final AiQuestionAnswerRepository aiQuestionAnswerRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public AiQuestionService(ChatClient.Builder builder,
                             CompetenceLevelRepository competenceLevelRepository,
                             QuestionTypeRepository questionTypeRepository,
                             QuestionRepository questionRepository,
                             AiQuestionRepository aiQuestionRepository,
                             AiQuestionAnswerRepository aiQuestionAnswerRepository,
                             CurrentUserService currentUserService,
                             UserRepository userRepository) {
        this.chatClient = builder.build();
        this.competenceLevelRepository = competenceLevelRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.questionRepository = questionRepository;
        this.aiQuestionRepository = aiQuestionRepository;
        this.aiQuestionAnswerRepository = aiQuestionAnswerRepository;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    public GeneratedQuestionDto generateQuestion(AskRequest askRequest) {
        // Õigused kontrollitakse enne AI päringut, et ilma õiguseta päringutele raha ei kuluks
        getValidQuestionAuthorId();
        String instructions = askRequest.getInstructions();

        List<CompetenceLevel> competenceLevels = competenceLevelRepository.findAllCompetenceLevelsBy(STATUS_ACTIVE.getCode());
        List<QuestionType> questionTypes = questionTypeRepository.findAll();

        QuestionTarget questionTarget = selectQuestionTarget(instructions, competenceLevels, questionTypes);
        Optional<CompetenceLevel> competenceLevel = findById(competenceLevels, questionTarget.competenceLevelId());
        Optional<QuestionType> questionType = questionTypes.stream()
                .filter(type -> type.getId().equals(questionTarget.questionTypeId()))
                .findFirst();

        if (competenceLevel.isEmpty() || questionType.isEmpty()) {
            return createClarifyingResponse(questionTarget.clarifyingQuestion());
        }

        AiGeneratedQuestion aiGeneratedQuestion = generateQuestionFor(competenceLevel.get(), questionType.get(), instructions);
        validateQuestion(aiGeneratedQuestion.title(), aiGeneratedQuestion.answers(), questionType.get());

        return createQuestionResponse(aiGeneratedQuestion, competenceLevel.get(), questionType.get());
    }

    @Transactional
    public Integer saveQuestion(AiQuestionSaveRequest aiQuestionSaveRequest) {
        Integer userId = getValidQuestionAuthorId();

        Integer competenceLevelId = aiQuestionSaveRequest.getCompetenceLevelId();
        CompetenceLevel competenceLevel = competenceLevelRepository.findById(competenceLevelId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("competenceLevelId", competenceLevelId));

        Integer questionTypeId = aiQuestionSaveRequest.getQuestionTypeId();
        QuestionType questionType = questionTypeRepository.findById(questionTypeId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("questionTypeId", questionTypeId));

        validateQuestion(aiQuestionSaveRequest.getTitle(), aiQuestionSaveRequest.getAnswers(), questionType);

        AiQuestion aiQuestion = saveAiQuestion(aiQuestionSaveRequest, competenceLevel, questionTypeId, userId);
        saveAiQuestionAnswers(aiQuestionSaveRequest.getAnswers(), aiQuestion);
        return aiQuestion.getId();
    }

    private Integer getValidQuestionAuthorId() {
        Integer userId = currentUserService.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new PrimaryKeyNotFoundException("userId", userId));
        String roleName = user.getRole().getName();

        boolean isAllowedToCreateQuestions = roleName.equals("ADMIN") || roleName.equals("HALDUR");
        if (!isAllowedToCreateQuestions) {
            throw new ForbiddenException(NO_PERMISSION_TO_CREATE_QUESTIONS.getMessage(), NO_PERMISSION_TO_CREATE_QUESTIONS.name());
        }
        return userId;
    }

    private QuestionTarget selectQuestionTarget(String instructions, List<CompetenceLevel> competenceLevels, List<QuestionType> questionTypes) {
        String competenceLevelList = String.join("\n", competenceLevels.stream()
                .map(competenceLevel -> "- id=" + competenceLevel.getId()
                        + ": " + competenceLevel.getCompetence().getName()
                        + " / " + competenceLevel.getLevel().getName())
                .toList());
        String questionTypeList = String.join("\n", questionTypes.stream()
                .map(questionType -> "- id=" + questionType.getId() + ": " + questionType.getName())
                .toList());

        String userPrompt = SELECTION_USER_PROMPT_TEMPLATE.formatted(competenceLevelList, questionTypeList, instructions);
        return callLlm(SELECTION_SYSTEM_PROMPT, userPrompt, QuestionTarget.class);
    }

    private Optional<CompetenceLevel> findById(List<CompetenceLevel> competenceLevels, Integer competenceLevelId) {
        return competenceLevels.stream()
                .filter(competenceLevel -> competenceLevel.getId().equals(competenceLevelId))
                .findFirst();
    }

    private GeneratedQuestionDto createClarifyingResponse(String clarifyingQuestion) {
        GeneratedQuestionDto generatedQuestionDto = new GeneratedQuestionDto();
        boolean hasClarifyingQuestion = clarifyingQuestion != null && !clarifyingQuestion.isBlank();
        generatedQuestionDto.setClarifyingQuestion(hasClarifyingQuestion ? clarifyingQuestion : DEFAULT_CLARIFYING_QUESTION);
        return generatedQuestionDto;
    }

    private AiGeneratedQuestion generateQuestionFor(CompetenceLevel competenceLevel, QuestionType questionType, String instructions) {
        List<Question> existingQuestions = questionRepository.findQuestionsBy(competenceLevel.getId(), STATUS_ACTIVE.getCode());
        String existingQuestionTitles = existingQuestions.isEmpty()
                ? "(none)"
                : String.join("\n", existingQuestions.stream().map(question -> "- " + question.getTitle()).toList());

        String userPrompt = QUESTION_USER_PROMPT_TEMPLATE.formatted(
                questionType.getName(),
                competenceLevel.getCompetence().getName(),
                competenceLevel.getCompetence().getDescription(),
                competenceLevel.getLevel().getName(),
                competenceLevel.getLevel().getDescription(),
                existingQuestionTitles,
                instructions);

        return callLlm(QUESTION_SYSTEM_PROMPT, userPrompt, AiGeneratedQuestion.class);
    }

    private void validateQuestion(String title, List<GeneratedAnswerDto> answers, QuestionType questionType) {
        if (title == null || title.isBlank() || answers == null || answers.size() < 2) {
            throw new IllegalStateException("AI ei suutnud korrektset küsimust genereerida, proovi uuesti");
        }

        long correctAnswerCount = answers.stream()
                .filter(answer -> Boolean.TRUE.equals(answer.getIsCorrect()))
                .count();

        boolean isMultipleChoice = questionType.getName().equals("MULTIPLE_CHOICE");
        boolean hasValidCorrectAnswerCount = isMultipleChoice ? correctAnswerCount >= 1 : correctAnswerCount == 1;
        if (!hasValidCorrectAnswerCount) {
            throw new IllegalStateException("AI genereeritud küsimusel on vale arv õigeid vastuseid, proovi uuesti");
        }
    }

    private AiQuestion saveAiQuestion(AiQuestionSaveRequest aiQuestionSaveRequest, CompetenceLevel competenceLevel, Integer questionTypeId, Integer userId) {
        AiQuestion aiQuestion = new AiQuestion();
        aiQuestion.setTitle(truncate(aiQuestionSaveRequest.getTitle(), 100));
        aiQuestion.setDescription(truncate(aiQuestionSaveRequest.getDescription(), 1000));
        aiQuestion.setCompetence(competenceLevel.getCompetence());
        aiQuestion.setCompetenceLevel(competenceLevel);
        aiQuestion.setQuestionTypeId(questionTypeId);
        aiQuestion.setStatus(STATUS_PENDING.getCode());
        aiQuestion.setCreatedBy(userId);
        Instant currentTime = Instant.now();
        aiQuestion.setCreatedAt(currentTime);
        aiQuestion.setUpdatedAt(currentTime);
        return aiQuestionRepository.save(aiQuestion);
    }

    private void saveAiQuestionAnswers(List<GeneratedAnswerDto> generatedAnswerDtos, AiQuestion aiQuestion) {
        for (GeneratedAnswerDto generatedAnswerDto : generatedAnswerDtos) {
            AiQuestionAnswer aiQuestionAnswer = new AiQuestionAnswer();
            aiQuestionAnswer.setAiQuestion(aiQuestion);
            aiQuestionAnswer.setAnswerText(truncate(generatedAnswerDto.getAnswerText(), 255));
            aiQuestionAnswer.setIsCorrect(Boolean.TRUE.equals(generatedAnswerDto.getIsCorrect()));
            aiQuestionAnswer.setStatus(STATUS_PENDING.getCode());
            // score ja feedback on andmebaasis NOT NULL, aga hinnatakse alles ülevaatamisel
            aiQuestionAnswer.setScore(0);
            aiQuestionAnswer.setFeedback(0);
            aiQuestionAnswerRepository.save(aiQuestionAnswer);
        }
    }

    private GeneratedQuestionDto createQuestionResponse(AiGeneratedQuestion aiGeneratedQuestion,
                                                        CompetenceLevel competenceLevel, QuestionType questionType) {
        GeneratedQuestionDto generatedQuestionDto = new GeneratedQuestionDto();
        generatedQuestionDto.setTitle(truncate(aiGeneratedQuestion.title(), 100));
        generatedQuestionDto.setDescription(truncate(aiGeneratedQuestion.description(), 1000));
        generatedQuestionDto.setAnswers(aiGeneratedQuestion.answers());
        generatedQuestionDto.setCompetenceLevelId(competenceLevel.getId());
        generatedQuestionDto.setCompetenceName(competenceLevel.getCompetence().getName());
        generatedQuestionDto.setLevelName(competenceLevel.getLevel().getName());
        generatedQuestionDto.setQuestionTypeId(questionType.getId());
        generatedQuestionDto.setQuestionTypeName(questionType.getName());
        return generatedQuestionDto;
    }

    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() > maxLength ? text.substring(0, maxLength) : text;
    }

    private <T> T callLlm(String systemPrompt, String userPrompt, Class<T> responseType) {
        T response = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .responseEntity(responseType)
                .getEntity();

        if (response == null) {
            throw new IllegalStateException("AI model returned an empty response");
        }

        return response;
    }
}
