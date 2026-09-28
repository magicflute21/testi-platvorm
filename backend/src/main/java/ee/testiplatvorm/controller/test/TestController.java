package ee.testiplatvorm.controller.test;


import ee.testiplatvorm.controller.test.dto.TestCreateRequestDto;
import ee.testiplatvorm.controller.test.dto.TestStartDto;
import ee.testiplatvorm.controller.test.dto.TestSummaryDto;
import ee.testiplatvorm.infrastructure.error.ApiError;
import ee.testiplatvorm.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TestController {

    private final TestService testService;

    @GetMapping("/tests")
    @Operation(summary = "Tagastab andmebaasist kõik testid.")
    @ApiResponse(
            responseCode = "200", description = "OK"
    )
    public List<TestSummaryDto> findAllTests() {
        List<TestSummaryDto> testSummaryDtos = testService.findAllTests();
        return testSummaryDtos;
    }

    @GetMapping("/tests/{testId}/start-info")
    public TestStartDto findTestStartInfo(@PathVariable Integer testId) {
       return testService.findTestStartInfo(testId);
    }

    @PostMapping("/tests")
    @Operation(summary = "Lisab andmebaasi uue testi.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Kohustuslikud väljad pole täidetud, 'errorCode': INCORRECT_INPUT",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Kui luba puudub, siis 'message': Sul puudub õigus testi luua, 'errorCode': NO_PERMISSION",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Kui andmebaasist vastet ei leita, siis 'message': Ei leidnud primary keyd väärtusega: {userId}, {competenceLevelId}, {questionId}, 'errorCode': PRIMARY_KEY_NOT_FOUND",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )

    })
    public void addNewTest(@RequestBody @Valid TestCreateRequestDto testCreateRequestDto) {
        testService.addNewTest(testCreateRequestDto);

    }
}
