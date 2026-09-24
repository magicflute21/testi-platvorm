package ee.testiplatvorm.controller.competence;

import ee.testiplatvorm.controller.competence.dto.CompetenceResponseDto;
import ee.testiplatvorm.service.CompetenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompetenceController {

    private final CompetenceService competenceService;

    @GetMapping("/api/competences")
    public List<CompetenceResponseDto> findCompetences() {
        return competenceService.findCompetences();
    }
}
