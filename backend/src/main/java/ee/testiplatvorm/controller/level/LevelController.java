package ee.testiplatvorm.controller.level;

import ee.testiplatvorm.controller.level.dto.LevelDto;
import ee.testiplatvorm.service.LevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @GetMapping("/api/levels")
    @Operation(summary = "Tagastab kõik tasemed, sorteeritud taseme numbri ja nime järgi.")
    @ApiResponse(
            responseCode = "200", description = "OK"
    )
    public List<LevelDto> findAllLevels() {
        return levelService.findAllLevels();
    }
}
