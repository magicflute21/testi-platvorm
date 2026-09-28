package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.testattempt.dto.SubmittedAnswersDto;
import ee.testiplatvorm.controller.testattempt.dto.TestAttemptAnswerDto;
import ee.testiplatvorm.controller.testattempt.dto.TestAttemptQuestionDto;
import ee.testiplatvorm.controller.testattempt.dto.TestAttemptResponseDto;
import ee.testiplatvorm.infrastructure.exception.ForbiddenException;
import ee.testiplatvorm.persistence.question.Question;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswer;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswerMapper;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswerRepository;
import ee.testiplatvorm.persistence.result.Result;
import ee.testiplatvorm.persistence.test.Test;
import ee.testiplatvorm.persistence.testquestion.TestQuestion;
import ee.testiplatvorm.persistence.testquestion.TestQuestionMapper;
import ee.testiplatvorm.persistence.testquestion.TestQuestionRepository;
import ee.testiplatvorm.persistence.usertest.UserTest;
import ee.testiplatvorm.persistence.usertest.UserTestMapper;
import ee.testiplatvorm.persistence.usertest.UserTestRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static ee.testiplatvorm.Error.NO_TEST_ASSIGNMENT_FOR_THIS_USER;
import static ee.testiplatvorm.Status.*;

@Service
@RequiredArgsConstructor
public class TestAttemptService {
    private final UserTestRepository userTestRepository;
    private final UserTestMapper userTestMapper;
    private final TestQuestionRepository testQuestionRepository;
    private final TestQuestionMapper testQuestionMapper;
    private final QuestionAnswerMapper questionAnswerMapper;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final CurrentUserService currentUserService;

    private static final int DECIMAL_POINTS = 2;

    public TestAttemptResponseDto getTestAttempt(Integer userId, Integer testId) {
        UserTest userTest = userTestRepository.getValidUserTestBy(userId, testId, STATUS_OPEN.getCode(), STATUS_ACTIVE.getCode(), STATUS_ACTIVE.getCode())
                .orElseThrow(() -> new ForbiddenException(NO_TEST_ASSIGNMENT_FOR_THIS_USER.getMessage(), NO_TEST_ASSIGNMENT_FOR_THIS_USER.name()));
        TestAttemptResponseDto testAttemptResponseDto = userTestMapper.toTestAttemptResponseDto(userTest);

        handleAddQuestions(testAttemptResponseDto, testId);

        return testAttemptResponseDto;
    }

    private void handleAddQuestions(TestAttemptResponseDto testAttemptResponseDto, Integer testId) {
        List<TestQuestion> testQuestions = testQuestionRepository.findQuestionsBy(testId);
        List<TestAttemptQuestionDto> testAttemptQuestionDtos = testQuestionMapper.toTestAttemptQuestionDtos(testQuestions);

        for (TestAttemptQuestionDto testAttemptQuestionDto : testAttemptQuestionDtos) {
            handleAddQuestionAnswers(testAttemptQuestionDto);
            List<Integer> selectedQuestionAnswerIds = new ArrayList<>();
            testAttemptQuestionDto.setSelectedQuestionAnswerIds(selectedQuestionAnswerIds);
        }
        testAttemptResponseDto.setQuestions(testAttemptQuestionDtos);
    }

    private void handleAddQuestionAnswers(TestAttemptQuestionDto testAttemptQuestionDto) {
        List<QuestionAnswer> questionAnswers = questionAnswerRepository.findAnswersBy(testAttemptQuestionDto.getQuestionId(), STATUS_ACTIVE.getCode());
        List<TestAttemptAnswerDto> testAttemptAnswerDtos = questionAnswerMapper.toQuestionAnswerDtos(questionAnswers);

        testAttemptQuestionDto.setAnswers(testAttemptAnswerDtos);
    }

    public void submitTest(Integer testId, List<SubmittedAnswersDto> submittedAnswers) {
        Integer userId = currentUserService.getUserId();
        UserTest userTest = userTestRepository.getValidUserTestBy(userId, testId, STATUS_OPEN.getCode(), STATUS_ACTIVE.getCode(), STATUS_ACTIVE.getCode())
                .orElseThrow(() -> new ForbiddenException(NO_TEST_ASSIGNMENT_FOR_THIS_USER.getMessage(), NO_TEST_ASSIGNMENT_FOR_THIS_USER.name()));

        Result result = new Result();
        handleCalculateScore(testId, submittedAnswers, result);
        handleResultStatus(userTest, result);
    }

    private void handleResultStatus(UserTest userTest, Result result) {
        Test test = userTest.getTest();

        BigDecimal testPassPercent = test.getPassPercent();
        boolean roundScoreUp = test.getRoundScoreUp();
        BigDecimal userScore = BigDecimal.valueOf(result.getScoreTotal());
        BigDecimal maxScore = BigDecimal.valueOf(result.getMaxScore());

        if (maxScore.compareTo(BigDecimal.ZERO) == 0) {
            // kui maxScore on 0, siis loeme testi automaatselt läbituks, mitte läbikukkunuks.
            result.setStatus(STATUS_PASSED.getCode());
            return;
        }

        BigDecimal userAchievedPercentage = userScore.multiply(BigDecimal.valueOf(100)).divide(maxScore, DECIMAL_POINTS, RoundingMode.HALF_UP);
        BigDecimal userAchievedEndResultPercentage = userAchievedPercentage;

        if (roundScoreUp) {
            userAchievedEndResultPercentage = userAchievedPercentage.setScale(0, RoundingMode.HALF_UP);
        }

        if (userAchievedEndResultPercentage.compareTo(testPassPercent) >= 0) {
            result.setStatus(STATUS_PASSED.getCode());
        } else {
            result.setStatus(STATUS_FAILED.getCode());
        }
    }

    private void handleCalculateScore(Integer testId, List<SubmittedAnswersDto> submittedAnswers, Result result) {
        Integer maxScore = 0;
        Integer userScore = 0;

        Map<Integer, List<Integer>> userAnswerIdsByQuestionId = getUserAnswerIdsByQuestionId(submittedAnswers);

        List<TestQuestion> testQuestions = testQuestionRepository.findQuestionsBy(testId);

        for (TestQuestion testQuestion : testQuestions) {
            Question question = testQuestion.getQuestion();
            Integer questionScore = question.getScore();

            maxScore += questionScore;

            if (isUserAnswerCorrect(question, userAnswerIdsByQuestionId)) {
                userScore += questionScore;
            }

        }
        result.setScoreTotal(userScore);
        result.setMaxScore(maxScore);
        result.setTotalQuestions(testQuestions.size());
    }

    private static @NonNull Map<Integer, List<Integer>> getUserAnswerIdsByQuestionId(List<SubmittedAnswersDto> submittedAnswers) {
        // make a map of question : userAnswerIds
        Map<Integer, List<Integer>> userAnswerIdsByQuestionId = new HashMap<>();

        for (SubmittedAnswersDto submittedAnswersDto : submittedAnswers) {
            userAnswerIdsByQuestionId.put(submittedAnswersDto.getQuestionId(), submittedAnswersDto.getAnswerIds());
        }

        return userAnswerIdsByQuestionId;
    }

    private boolean isUserAnswerCorrect(Question question, Map<Integer, List<Integer>> userAnswerIdsByQuestionId) {
        List<Integer> userAnswerIds = userAnswerIdsByQuestionId.get(question.getId());

        if (userAnswerIds == null || userAnswerIds.isEmpty()) {
            return false;
        }

        List<Integer> correctAnswerIds = questionAnswerRepository.findCorrectAnswerIdsBy(question.getId(), STATUS_ACTIVE.getCode());
        return new HashSet<>(correctAnswerIds).equals(new HashSet<>(userAnswerIds));
    }
}
