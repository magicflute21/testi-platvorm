package ee.testiplatvorm.controller.test;


import ee.testiplatvorm.controller.test.dto.TestSummaryDto;
import ee.testiplatvorm.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/api/tests")
    @Operation(summary = "Tagastab andmebaasist kõik testid.")
    @ApiResponse(
            responseCode = "200", description = "OK"
    )
    public List<TestSummaryDto> findAllTests() {
        List<TestSummaryDto> testSummaryDtos = testService.findAllTests();
        return testSummaryDtos;
    }
}
