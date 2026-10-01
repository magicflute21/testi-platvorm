package ee.testiplatvorm.controller.aiquestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AskRequest {
    @NotBlank
    @Size(max = 500)
    private String instructions;
}
