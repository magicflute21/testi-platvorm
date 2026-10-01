package ee.testiplatvorm.controller.questiontype;


import ee.testiplatvorm.controller.questiontype.dto.QuestionTypeDto;
import ee.testiplatvorm.service.QuestionTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor

public class QuestionTypeController {

    private final QuestionTypeService questionTypeService;

    @Operation (summary = "Tagastab kõik küsimuste tüübid")
    @ApiResponse(
            responseCode = "200",
            description = "OK"
    )
    @GetMapping("/api/question-types")
    public List<QuestionTypeDto> findQuestionTypes() {
        List<QuestionTypeDto> questionTypeDtos = questionTypeService.findQuestionTypes();
        return questionTypeDtos;

    }

}
