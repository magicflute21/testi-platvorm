package ee.testiplatvorm.controller.aiquestion;

import ee.testiplatvorm.controller.aiquestion.dto.AiQuestionGenerationResponse;
import ee.testiplatvorm.controller.aiquestion.dto.AiQuestionSaveRequest;
import ee.testiplatvorm.controller.aiquestion.dto.AskRequest;
import ee.testiplatvorm.service.AiQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiQuestionController {

    private final AiQuestionService aiQuestionService;

    @PostMapping("/api/ai-questions/generate")
    @Operation(summary = "AI tuvastab vabatekstist kompetentsi, taseme, küsimuse tüübi ja küsimuste arvu (kuni 5) ning genereerib küsimused koos vastusevariantidega. Kasutaja saab tunnis genereerida kuni 30 küsimust. Midagi ei salvestata - salvestamiseks tuleb kasutada POST /api/ai-questions. Kui kompetentsi või taset ei õnnestu tuvastada, tagastatakse ainult clarifyingQuestion.")
    @ApiResponse(responseCode = "200", description = "OK - kas genereeritud küsimused (questions) või täpsustav küsimus (clarifyingQuestion)")
    @ApiResponse(responseCode = "401", description = "Kasutaja pole sisse logitud")
    @ApiResponse(responseCode = "403", description = "Kasutajal puudub õigus küsimusi luua (lubatud ainult ADMIN ja HALDUR rollile)")
    @ApiResponse(responseCode = "429", description = "Tunnilimiit (30 küsimust) on täis")
    public AiQuestionGenerationResponse generateQuestions(@RequestBody @Valid AskRequest askRequest) {
        AiQuestionGenerationResponse aiQuestionGenerationResponse = aiQuestionService.generateQuestions(askRequest);
        return aiQuestionGenerationResponse;
    }

    @PostMapping("/api/ai-questions")
    @Operation(summary = "Salvestab kasutaja kinnitatud AI küsimuse ai_question ja ai_question_answer tabelitesse staatusega 'P' (ootab ülevaatust). Tagastab loodud ai_question id.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Vigased sisendandmed või küsimus ei vasta reeglitele")
    @ApiResponse(responseCode = "401", description = "Kasutaja pole sisse logitud")
    @ApiResponse(responseCode = "403", description = "Kasutajal puudub õigus küsimusi luua (lubatud ainult ADMIN ja HALDUR rollile)")
    @ApiResponse(responseCode = "404", description = "competenceLevelId või questionTypeId ei leitud")
    public Integer saveQuestion(@RequestBody @Valid AiQuestionSaveRequest aiQuestionSaveRequest) {
        Integer aiQuestionId = aiQuestionService.saveQuestion(aiQuestionSaveRequest);
        return aiQuestionId;
    }
}
