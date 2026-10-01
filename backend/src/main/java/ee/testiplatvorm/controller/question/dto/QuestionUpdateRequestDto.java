package ee.testiplatvorm.controller.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Küsimuse muutmine: muuta saab ainult pealkirja, kirjeldust ja staatust, vastusevariante mitte.
 */
@Data
public class QuestionUpdateRequestDto {
    @NotBlank
    @Size(max = 100)
    private String questionTitle;
    @NotBlank
    @Size(max = 1000)
    private String questionDescription;
    @NotBlank
    @Pattern(regexp = "[AI]")
    private String questionStatus;
}
