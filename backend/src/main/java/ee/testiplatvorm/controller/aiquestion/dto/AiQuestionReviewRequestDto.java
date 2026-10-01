package ee.testiplatvorm.controller.aiquestion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AI küsimuse ülevaatus: hinnang 1-5, tagasiside ja otsus (kinnita või lükka tagasi).
 */
@Data
public class AiQuestionReviewRequestDto {
    @NotNull
    @Min(1)
    @Max(5)
    private Integer score;
    @Size(max = 255)
    private String feedback;
    @NotNull
    private Boolean approved;
}
