package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.test.dto.TestSummaryDto;
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

    public void findAllTests() {
        List<Test> tests = testRepository.findAll();
        List<TestSummaryDto> testsDto = testMapper.toTestSummaryDto(List <Test> tests);



    }
}
