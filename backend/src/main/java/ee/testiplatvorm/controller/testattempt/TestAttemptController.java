package ee.testiplatvorm.controller.testattempt;

import ee.testiplatvorm.controller.testattempt.dto.SubmittedAnswersDto;
import ee.testiplatvorm.controller.testattempt.dto.TestAttemptResponseDto;
import ee.testiplatvorm.service.CurrentUserService;
import ee.testiplatvorm.service.TestAttemptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tests/{testId}")

public class TestAttemptController {

    private final TestAttemptService testAttemptService;
    private final CurrentUserService currentUserService;

    @GetMapping("/attempt")
    @Operation(summary = "Tagastab testi info koos küsimuste ja vastusevariantidega. Parameetrina võtab sisse testi id ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Kui sisselogitud kasutajale ei ole seda testi määratud (või määramine/test/kasutaja pole aktiivne), siis 'message': Kasutajale ei ole vastavat testi määratud, 'errorCode': NO_TEST_ASSIGNMENT_FOR_THIS_USER"
            )

    })
    public TestAttemptResponseDto getTestAttempt(@PathVariable Integer testId) {
        Integer userId = currentUserService.getUserId();
        return testAttemptService.getTestAttempt(userId, testId);
    }

    @PostMapping("/complete")
    @Operation(summary = "Hindab testi tulemust ja märgib selle lõpetatuks")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Kui sisselogitud kasutajale ei ole seda testi määratud (või määramine/test/kasutaja pole aktiivne), siis 'message': Kasutajale ei ole vastavat testi määratud, 'errorCode': NO_TEST_ASSIGNMENT_FOR_THIS_USER"
            )

    })
    public void submitTest(@PathVariable Integer testId, @RequestBody List<SubmittedAnswersDto> submittedAnswers) {
        testAttemptService.submitTest(testId, submittedAnswers);
    }
}
