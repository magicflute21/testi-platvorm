package ee.testiplatvorm.controller.test.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TestQuestionDto {
    @NotNull
    private Integer questionId;
}
