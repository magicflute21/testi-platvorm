package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.aiquestion.dto.GeneratedAnswerDto;
import ee.testiplatvorm.persistence.questiontype.QuestionType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

// AI loodud küsimuste ühised reeglid - kasutavad nii AI chat (AiQuestionService) kui ka taseme põhine loomine (AiQuestionByLevelService)
@Service
public class AiQuestionValidationService {

    public static final int MAX_ANSWER_COUNT = 5;
    private static final int MIN_ANSWER_COUNT = 2;
    // Kirjelduses on lubatud kuni 10-realine koodinäide + paar rida juhist
    private static final int MAX_DESCRIPTION_LINE_COUNT = 12;

    // Märgendid, mis reedavad vastuse õigsuse, nt "(õige)", "- vale", "✓"
    private static final Pattern ANSWER_CORRECTNESS_HINT = Pattern.compile(
            "[✓✔✗✘]|\\b(õige|vale|correct|wrong|incorrect)\\s*(vastus|answer)?\\s*[)\\]]|[(\\[]\\s*(õige|vale|correct|wrong|incorrect)\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public boolean isValidQuestion(String title, String description, List<GeneratedAnswerDto> answers, QuestionType questionType) {
        if (title == null || title.isBlank() || answers == null
                || answers.size() < MIN_ANSWER_COUNT || answers.size() > MAX_ANSWER_COUNT) {
            return false;
        }

        if (description != null && description.lines().count() > MAX_DESCRIPTION_LINE_COUNT) {
            return false;
        }

        boolean hasCorrectnessHint = answers.stream()
                .anyMatch(answer -> answer.getAnswerText() == null
                        || ANSWER_CORRECTNESS_HINT.matcher(answer.getAnswerText()).find());
        if (hasCorrectnessHint) {
            return false;
        }

        long correctAnswerCount = answers.stream()
                .filter(answer -> Boolean.TRUE.equals(answer.getIsCorrect()))
                .count();

        boolean isMultipleChoice = questionType.getName().equals("MULTIPLE_CHOICE");
        return isMultipleChoice ? correctAnswerCount >= 1 : correctAnswerCount == 1;
    }
}
