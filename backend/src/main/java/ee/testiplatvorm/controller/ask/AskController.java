package ee.testiplatvorm.controller.ask;

import ee.testiplatvorm.controller.ask.dto.AskRequest;
import ee.testiplatvorm.controller.ask.dto.AskResponse;
import ee.testiplatvorm.infrastructure.error.ApiError;
import ee.testiplatvorm.service.CurrentUserService;
import ee.testiplatvorm.service.NlToSqlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AskController {

    private final NlToSqlService nlToSqlService;
    private final CurrentUserService currentUserService;

    @PostMapping("/api/ask")
    @Operation(summary = "Vastab loomulikus keeles küsimusele kasutajate andmete kohta. AI genereerib SELECT päringu, käivitab selle ja võtab tulemuse kokku.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Küsimus puudub, on liiga pikk või AI ei suutnud sellele lubatud SELECT päringut koostada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "401", description = "Kasutaja ei ole sisse loginud"
            )
    })
    public AskResponse ask(@RequestBody @Valid AskRequest askRequest) {
        currentUserService.getUserId();
        return nlToSqlService.ask(askRequest.question());
    }
}
