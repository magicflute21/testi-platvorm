package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.test.dto.TestCreateRequestDto;
import ee.testiplatvorm.controller.test.dto.TestQuestionDto;
import ee.testiplatvorm.controller.test.dto.TestStartDto;
import ee.testiplatvorm.controller.test.dto.TestSummaryDto;
import ee.testiplatvorm.infrastructure.exception.ForbiddenException;
import ee.testiplatvorm.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.testiplatvorm.persistence.competencelevel.CompetenceLevel;
import ee.testiplatvorm.persistence.competencelevel.CompetenceLevelRepository;
import ee.testiplatvorm.persistence.question.Question;
import ee.testiplatvorm.persistence.question.QuestionRepository;
import ee.testiplatvorm.persistence.test.Test;
import ee.testiplatvorm.persistence.test.TestMapper;
import ee.testiplatvorm.persistence.test.TestRepository;
import ee.testiplatvorm.persistence.testquestion.TestQuestion;
import ee.testiplatvorm.persistence.testquestion.TestQuestionRepository;
import ee.testiplatvorm.persistence.user.User;
import ee.testiplatvorm.persistence.user.UserRepository;
import ee.testiplatvorm.persistence.usertest.UserTest;
import ee.testiplatvorm.persistence.usertest.UserTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static ee.testiplatvorm.Error.NO_PERMISSION;
import static ee.testiplatvorm.Error.NO_TEST_ASSIGNMENT_FOR_THIS_USER;
import static ee.testiplatvorm.Status.STATUS_ACTIVE;
import static ee.testiplatvorm.Status.STATUS_OPEN;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;
    private final UserRepository userRepository;
    private final CompetenceLevelRepository competenceLevelRepository;
    private final UserTestRepository userTestRepository;
    private final CurrentUserService currentUserService;
    private final QuestionRepository questionRepository;
    private final TestQuestionRepository testQuestionRepository;

    public List<TestSummaryDto> findAllTests() {
        List<Test> tests = testRepository.findAll();
        List<TestSummaryDto> testSummaryDtos = testMapper.toTestSummaryDtos(tests);
        return testSummaryDtos;
    }

    public TestStartDto findTestStartInfo(Integer testId) {
        Integer userId = currentUserService.getUserId();

        UserTest userTest = userTestRepository.getValidUserTestBy(userId, testId, STATUS_OPEN.getCode(), STATUS_ACTIVE.getCode(), STATUS_ACTIVE.getCode())
                .orElseThrow(() -> new ForbiddenException(NO_TEST_ASSIGNMENT_FOR_THIS_USER.getMessage(), NO_TEST_ASSIGNMENT_FOR_THIS_USER.name()));
        Test test = userTest.getTest();
        return testMapper.toTestStartDto(test);
    }

    @Transactional
    public void createTest(TestCreateRequestDto testCreateRequestDto) {
        Integer userId = testCreateRequestDto.getUserId();

        User user = userRepository.findById(userId).orElseThrow(() -> new PrimaryKeyNotFoundException("userId", userId));
        String roleName = user.getRole().getName();

        boolean isAllowedToCreateTest = roleName.equals("ADMIN") || roleName.equals("HALDUR");
        if (!isAllowedToCreateTest) {
            throw new ForbiddenException(NO_PERMISSION.getMessage(), NO_PERMISSION.name());
        }

        Test test = testMapper.toTest(testCreateRequestDto);

        Integer competenceLevelId = testCreateRequestDto.getCompetenceLevelId();
        CompetenceLevel competenceLevel = competenceLevelRepository.findById(competenceLevelId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("competenceLevelId", competenceLevelId));
        test.setCompetenceLevel(competenceLevel);

        test.setCompetence(competenceLevel.getCompetence());
        test.setCreatedBy(user);
        test.setStatus(STATUS_ACTIVE.getCode());
        OffsetDateTime currentTime = OffsetDateTime.now();
        test.setCreatedAt(currentTime);
        test.setUpdatedAt(currentTime);
        Test savedTest = testRepository.save(test);

        List<TestQuestionDto> questionDtos = testCreateRequestDto.getQuestions();
        for (int i = 0; i < questionDtos.size(); i++) {
            Integer questionId = questionDtos.get(i).getQuestionId();

            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new PrimaryKeyNotFoundException("questionId", questionId));

            TestQuestion testQuestion = new TestQuestion();
            testQuestion.setTest(savedTest);
            testQuestion.setQuestion(question);
            testQuestion.setPosition(i + 1);
            testQuestion.setAddedBy(user);
            testQuestion.setCreatedAt(currentTime);
            testQuestion.setUpdatedAt(currentTime);

            testQuestionRepository.save(testQuestion);
        }
    }

}
