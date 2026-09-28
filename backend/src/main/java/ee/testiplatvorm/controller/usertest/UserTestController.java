package ee.testiplatvorm.controller.usertest;


import ee.testiplatvorm.controller.usertest.dto.UserTestSummaryDto;
import ee.testiplatvorm.service.UserTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor

public class UserTestController {


    private final UserTestService userTestService;


    @Operation(summary="Tagastab sisse logitud kasutajale kõik tema userId-ga seotud testid")
    @ApiResponses( value = {
            @ApiResponse (
                    responseCode = "200",
                    description = "OK"
                    ),
            @ApiResponse (
                    responseCode = "401",
                    description = "Kasutaja pole sisse logitud"
            )
    }
    )
    @GetMapping("/api/me/user-tests")
    public List<UserTestSummaryDto> findUserTests() {
        List<UserTestSummaryDto> userTestSummaryDtos = userTestService.findUserTests();
        return userTestSummaryDtos;
    }


}
