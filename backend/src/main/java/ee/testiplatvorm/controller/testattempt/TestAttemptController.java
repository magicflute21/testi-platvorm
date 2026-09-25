package ee.testiplatvorm.controller.testattempt;

import ee.testiplatvorm.controller.testattempt.dto.TestAttemptResponseDto;
import ee.testiplatvorm.service.TestAttemptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")

public class TestAttemptController {

    private final TestAttemptService testAttemptService;

    @GetMapping("/user-tests/{userTestId}/attempt")
    @Operation(summary = "Tagastab testi info koos küsimuste ja vastusevariantidega. Parameetrina võtab sisse user_test id ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Kui userTestId-le ei ole andmebaasis vastet, siis 'message': Ei leidnud primary keyd 'userTestId' väärtusega: {userTestId}, 'errorCode': PRIMARY_KEY_NOT_FOUND"
            )

    })
    public TestAttemptResponseDto getTestAttempt(@PathVariable Integer userTestId) {
        return testAttemptService.getTestAttempt(userTestId);
    }
}
