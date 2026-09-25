package ee.testiplatvorm.controller.question;

import ee.testiplatvorm.controller.question.dto.QuestionResponseDto;
import ee.testiplatvorm.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/api/questions")
    @Operation(summary = "Tagastatakse valitud kompetentsi taseme aktiivsed küsimused (question tabeli status = 'A'). " +
            "Näide vastab päringule competenceLevelId=1. Kui küsimusi pole, tagastatakse tühi list.")
    @ApiResponse(
            responseCode = "200", description = "OK"
    )
    public List<QuestionResponseDto> findQuestionsById(@RequestParam Integer questionId) {

        List<QuestionResponseDto> questionResponseDtos = questionService.findQuestionsById(questionId);
        return questionResponseDtos;

    }
}
