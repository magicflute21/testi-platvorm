package ee.testiplatvorm.controller.question;

import ee.testiplatvorm.controller.question.dto.QuestionCreateRequestDto;
import ee.testiplatvorm.controller.question.dto.QuestionBankDto;
import ee.testiplatvorm.controller.question.dto.QuestionCreateRequestDto;
import ee.testiplatvorm.controller.question.dto.QuestionResponseDto;
import ee.testiplatvorm.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/api/questions")
    @Operation(summary = "Tagastatakse valitud kompetentsi taseme aktiivsed küsimused (question tabeli status = 'A'). Kui küsimusi pole, tagastatakse tühi list.")
    @ApiResponse(
            responseCode = "200", description = "OK"
    )
    public List<QuestionResponseDto> findQuestionsBy(@RequestParam Integer competenceLevelId) {
        List<QuestionResponseDto> questionResponseDtos = questionService.findQuestionsBy(competenceLevelId);
        return questionResponseDtos;
    }

    @GetMapping("/api/question-bank")
    @Operation(summary = "Tagastatakse kõik küsimused olenemata staatusest, koos vastusevariantidega. competenceId on valikuline - kui see puudub, tagastatakse kõigi kompetentside küsimused.")
    @ApiResponse(
            responseCode = "200", description = "OK"
    )
    public List<QuestionBankDto> findAllQuestionsBy(@RequestParam(required = false) Integer competenceId) {
        List<QuestionBankDto> questionBankDtos = questionService.findAllQuestionsBy(competenceId);
        return questionBankDtos;
    }

    @PostMapping("/api/questions")
    @Operation(summary = "Luuakse uus küsimus koos vastusevariantidega. Tagastatakse loodud küsimuse id.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Õigete vastuste arv ei vasta küsimuse tüübile (INVALID_CORRECT_ANSWER_COUNT)")
    @ApiResponse(responseCode = "401", description = "Kasutaja pole sisse logitud")
    @ApiResponse(responseCode = "403", description = "Kasutaja roll pole ADMIN ega HALDUR (NO_PERMISSION_TO_CREATE_QUESTIONS)")
    @ApiResponse(responseCode = "404", description = "competenceLevelId või questionTypeId järgi rida ei leitud (PRIMARY_KEY_NOT_FOUND)")
    public Integer createQuestion(@RequestBody QuestionCreateRequestDto questionCreateRequestDto) {
        Integer questionId = questionService.createQuestion(questionCreateRequestDto);
        return questionId;
    }
}
