package ee.testiplatvorm.controller.aiquestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AiQuestionSaveRequest {
    @NotNull
    private Integer competenceLevelId;
    @NotNull
    private Integer questionTypeId;
    @NotBlank
    @Size(max = 100)
    private String title;
    @Size(max = 1000)
    private String description;
    @NotEmpty
    private List<GeneratedAnswerDto> answers;
}
