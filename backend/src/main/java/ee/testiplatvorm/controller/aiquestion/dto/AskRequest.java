package ee.testiplatvorm.controller.aiquestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AskRequest {
    @NotBlank
    @Size(max = 1000)
    private String instructions;
}
