package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.test.dto.TestStartDto;
import ee.testiplatvorm.controller.test.dto.TestSummaryDto;
import ee.testiplatvorm.infrastructure.exception.ForbiddenException;
import ee.testiplatvorm.persistence.test.Test;
import ee.testiplatvorm.persistence.test.TestMapper;
import ee.testiplatvorm.persistence.test.TestRepository;
import ee.testiplatvorm.persistence.usertest.UserTest;
import ee.testiplatvorm.persistence.usertest.UserTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static ee.testiplatvorm.Error.NO_TEST_ASSIGNMENT_FOR_THIS_USER;
import static ee.testiplatvorm.Status.STATUS_ACTIVE;
import static ee.testiplatvorm.Status.STATUS_OPEN;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;
    private final UserTestRepository userTestRepository;
    private final CurrentUserService currentUserService;

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

}
