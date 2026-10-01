package ee.testiplatvorm.controller.result;

import ee.testiplatvorm.controller.result.dto.ResultResponseDto;
import ee.testiplatvorm.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @GetMapping("/api/result")
    @Operation(summary = "Tagastab ühe testi tulemused. Parameetrina võtab sisse kasutajale määratud testi id (userTestId)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Kui antud userTestId-ga testil tulemust ei leitud, siis 'message': Tulemust ei leitud, 'errorCode': NO_RESULT_FOUND"
            )

    })
    public ResultResponseDto findTestResult(@RequestParam Integer userTestId) {
       return resultService.findTestResult(userTestId);
    }
}
