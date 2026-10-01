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

    public void createQuestion(@RequestBody QuestionCreateRequestDto questionCreateRequestDto) {
        questionService.createQuestion(questionCreateRequestDto);


    }
}
