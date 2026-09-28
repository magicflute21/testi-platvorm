package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.test.dto.TestCreateRequestDto;
import ee.testiplatvorm.controller.test.dto.TestStartDto;
import ee.testiplatvorm.controller.test.dto.TestSummaryDto;
import ee.testiplatvorm.infrastructure.exception.ForbiddenException;
import ee.testiplatvorm.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.testiplatvorm.persistence.competencelevel.CompetenceLevel;
import ee.testiplatvorm.persistence.competencelevel.CompetenceLevelRepository;
import ee.testiplatvorm.persistence.test.Test;
import ee.testiplatvorm.persistence.test.TestMapper;
import ee.testiplatvorm.persistence.test.TestRepository;
import ee.testiplatvorm.persistence.user.User;
import ee.testiplatvorm.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static ee.testiplatvorm.Error.NO_PERMISSION;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;
    private final UserRepository userRepository;
    private final CompetenceLevelRepository competenceLevelRepository;

    public List<TestSummaryDto> findAllTests() {
        List<Test> tests = testRepository.findAll();
        List<TestSummaryDto> testSummaryDtos = testMapper.toTestSummaryDtos(tests);
        return testSummaryDtos;
    }

    public TestStartDto findTestStartInfo(Integer testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("testId", testId));
        TestStartDto testStartDto = testMapper.toTestStartDto(test);
        return testStartDto;
    }

    public void addNewTest(TestCreateRequestDto testCreateRequestDto) {
        Integer userId = testCreateRequestDto.getUserId();

        User user = userRepository.findById(userId).orElseThrow( () -> new PrimaryKeyNotFoundException("userId", userId));
        String roleName = user.getRole().getName();

        boolean isAllowedToCreateTest = roleName.equals("ADMIN") || roleName.equals("HALDUR");
        if (!isAllowedToCreateTest) {
            throw new ForbiddenException(NO_PERMISSION.getMessage(), NO_PERMISSION.name());
        }

        Test test = testMapper.toTest(testCreateRequestDto);

        Integer competenceLevelId = testCreateRequestDto.getCompetenceLevelId();
        CompetenceLevel competenceLevel = competenceLevelRepository.findById(competenceLevelId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("competenceLevelId", competenceLevelId));
    }
}
