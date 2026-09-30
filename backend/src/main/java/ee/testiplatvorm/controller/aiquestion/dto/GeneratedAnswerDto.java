package ee.testiplatvorm.controller.aiquestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GeneratedAnswerDto {
    @NotBlank
    @Size(max = 255)
    private String answerText;
    @NotNull
    private Boolean isCorrect;
}
