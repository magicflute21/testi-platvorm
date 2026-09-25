package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.testattempt.dto.TestAttemptAnswerDto;
import ee.testiplatvorm.controller.testattempt.dto.TestAttemptQuestionDto;
import ee.testiplatvorm.controller.testattempt.dto.TestAttemptResponseDto;
import ee.testiplatvorm.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswer;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswerMapper;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswerRepository;
import ee.testiplatvorm.persistence.testquestion.TestQuestion;
import ee.testiplatvorm.persistence.testquestion.TestQuestionMapper;
import ee.testiplatvorm.persistence.testquestion.TestQuestionRepository;
import ee.testiplatvorm.persistence.usertest.UserTest;
import ee.testiplatvorm.persistence.usertest.UserTestMapper;
import ee.testiplatvorm.persistence.usertest.UserTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static ee.testiplatvorm.Status.STATUS_ACTIVE;
import static ee.testiplatvorm.Status.STATUS_OPEN;

@Service
@RequiredArgsConstructor
public class TestAttemptService {
    private final UserTestRepository userTestRepository;
    private final UserTestMapper userTestMapper;
    private final TestQuestionRepository testQuestionRepository;
    private final TestQuestionMapper testQuestionMapper;
    private final QuestionAnswerMapper questionAnswerMapper;
    private final QuestionAnswerRepository questionAnswerRepository;

    public TestAttemptResponseDto getTestAttempt(Integer userTestId) {
        UserTest userTest = userTestRepository.getValidUserTestBy(userTestId, STATUS_OPEN.getCode(), STATUS_ACTIVE.getCode())
                .orElseThrow(() -> new PrimaryKeyNotFoundException("userTestId", userTestId));
        TestAttemptResponseDto testAttemptResponseDto = userTestMapper.toTestAttemptResponseDto(userTest);

        Integer testId = userTest.getTest().getId();

        handleAddQuestions(testAttemptResponseDto, testId);

        return testAttemptResponseDto;
    }

    private void handleAddQuestions(TestAttemptResponseDto testAttemptResponseDto, Integer testId) {
        List<TestQuestion> testQuestions = testQuestionRepository.findQuestionsBy(testId);
        List<TestAttemptQuestionDto> testAttemptQuestionDtos = testQuestionMapper.toTestAttemptQuestionDtos(testQuestions);

        for (TestAttemptQuestionDto testAttemptQuestionDto : testAttemptQuestionDtos){
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
}
