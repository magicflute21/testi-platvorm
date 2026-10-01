package ee.testiplatvorm.controller.result.dto;

import ee.testiplatvorm.persistence.result.Result;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO for {@link Result}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultResponseDto implements Serializable {
    private Integer id;
    private Integer userTestId;
    private OffsetDateTime userTestOpensAt;
    private OffsetDateTime userTestCreatedAt;
    @NotNull
    private String status;
    @NotNull
    private Integer maxScore;
    @NotNull
    private Integer scoreTotal;
    @NotNull
    private OffsetDateTime completedAt;
    @NotNull
    private OffsetDateTime startedAt;
    @NotNull
    private Integer totalQuestions;
    @NotNull
    private Integer questionsAnswered;
    @NotNull
    private BigDecimal userAchievedScorePercentage;
}