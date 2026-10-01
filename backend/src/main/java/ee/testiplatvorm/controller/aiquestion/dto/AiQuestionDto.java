package ee.testiplatvorm.controller.aiquestion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * DTO for {@link ee.testiplatvorm.persistence.aiquestion.AiQuestion}
 */
@Value
public class AiQuestionDto implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 100)
    String title;
    @NotNull
    @Size(max = 1000)
    String description;
    Integer competenceId;
    Integer competenceLevelId;
    @NotNull
    Integer questionTypeId;
    @NotNull
    String status;
    @NotNull
    OffsetDateTime createdAt;
    @NotNull
    Integer createdBy;
    @NotNull
    OffsetDateTime updatedAt;
    Integer score;
    @Size(max = 255)
    String feedback;
    Boolean isGood;
}
