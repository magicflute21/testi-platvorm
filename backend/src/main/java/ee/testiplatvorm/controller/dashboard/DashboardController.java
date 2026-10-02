package ee.testiplatvorm.controller.dashboard;

import ee.testiplatvorm.controller.dashboard.dto.DashboardDto;
import ee.testiplatvorm.infrastructure.error.ApiError;
import ee.testiplatvorm.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/dashboard")
    @Operation(summary = "Tagastab töölaua lühiülevaate: sisselogitud kasutajale määratud ja veel sooritamata testide arv, viimase recentDays päeva jooksul lisandunud testide ja küsimuste arv ning ülevaatust ootavate AI küsimuste arv.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Kasutaja ei ole sisse logitud"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Kui kasutaja roll ei ole ADMIN ega HALDUR, siis 'message': Sul puudub õigus töölauda vaadata, 'errorCode': NO_PERMISSION_TO_VIEW_DASHBOARD",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public DashboardDto findDashboard() {
        return dashboardService.findDashboard();
    }
}
