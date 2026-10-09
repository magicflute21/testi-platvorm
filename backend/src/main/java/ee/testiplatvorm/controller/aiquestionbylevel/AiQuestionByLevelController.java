package ee.testiplatvorm.controller.aiquestionbylevel;

import ee.testiplatvorm.service.AiQuestionByLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiQuestionByLevelController {

    private final AiQuestionByLevelService aiQuestionByLevelService;

    @PostMapping("/api/ai-questions/generate-by-level")
    public void generateQuestionsByCompetenceAndLevel() {
        aiQuestionByLevelService.generateQuestionsByCompetenceAndLevel();
    }
}
