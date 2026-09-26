package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.test.dto.TestStartDto;
import ee.testiplatvorm.controller.test.dto.TestSummaryDto;
import ee.testiplatvorm.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.testiplatvorm.persistence.test.Test;
import ee.testiplatvorm.persistence.test.TestMapper;
import ee.testiplatvorm.persistence.test.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;

    public List<TestSummaryDto> findAllTests() {
        List<Test> tests = testRepository.findAll();
        List<TestSummaryDto> testSummaryDtos = testMapper.toTestSummaryDtos(tests);
        return testSummaryDtos;
    }

    public TestStartDto findTestStartInfo(Integer testId) {
       Test test =  testRepository.findById(testId)
               .orElseThrow(() -> new PrimaryKeyNotFoundException("testId", testId));
        TestStartDto testStartDto = testMapper.toTestStartDto(test);
        return testStartDto;
    }

}
