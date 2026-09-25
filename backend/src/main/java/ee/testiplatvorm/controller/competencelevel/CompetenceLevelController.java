package ee.testiplatvorm.controller.competencelevel;

import ee.testiplatvorm.controller.competencelevel.dto.CompetenceLevelResponseDto;
import ee.testiplatvorm.service.CompetenceLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompetenceLevelController {

    private final CompetenceLevelService competenceLevelService;

    @GetMapping("/api/competence-levels")
    @Operation(summary = "Tagastatakse valitud kompetentsi aktiivsed tasemed (status = 'A')")
    @ApiResponse(
            responseCode = "200", description = "OK"
    )
    public List<CompetenceLevelResponseDto> findCompetenceLevels(@RequestParam Integer competenceId) {
        List<CompetenceLevelResponseDto> competenceLevelResponseDtos = competenceLevelService.findCompetenceLevels(competenceId);
        return competenceLevelResponseDtos;
    }

}
