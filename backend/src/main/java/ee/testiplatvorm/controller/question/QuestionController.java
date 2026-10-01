package ee.testiplatvorm.controller.question;

import ee.testiplatvorm.controller.question.dto.QuestionCreateRequestDto;
import ee.testiplatvorm.controller.question.dto.QuestionBankDto;
import ee.testiplatvorm.controller.question.dto.QuestionCreateRequestDto;
import ee.testiplatvorm.controller.question.dto.QuestionCreateRequestDto;
import ee.testiplatvorm.controller.question.dto.QuestionResponseDto;
import ee.testiplatvorm.controller.question.dto.QuestionUpdateRequestDto;
import ee.testiplatvorm.infrastructure.error.ApiError;
import ee.testiplatvorm.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;

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

    @PutMapping("/api/questions/{questionId}")
    @Operation(summary = "Muudab küsimuse pealkirja, kirjeldust ja staatust (A või I). Vastusevariante muuta ei saa.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Kohustuslikud väljad pole täidetud, liiga pikad või staatus pole A/I, 'errorCode': INCORRECT_INPUT",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ei leidnud primary keyd 'questionId' väärtusega ?",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public void updateQuestion(@PathVariable Integer questionId, @RequestBody @Valid QuestionUpdateRequestDto questionUpdateRequestDto) {
        questionService.updateQuestion(questionId, questionUpdateRequestDto);
    }

    @DeleteMapping("/api/questions/{questionId}")
    @Operation(summary = "Kustutab küsimuse: küsimus jääb andmebaasi alles, aga staatuseks muudetakse I (mitteaktiivne).")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ei leidnud primary keyd 'questionId' väärtusega ?",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public void deleteQuestion(@PathVariable Integer questionId) {
        questionService.deleteQuestion(questionId);
    }
}
