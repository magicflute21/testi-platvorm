package ee.testiplatvorm.controller.test.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class TestDetailDto {
    private Integer testId;
    private String title;
    private String shortDescription;
    private String description;
    private String competence;
    private String competenceLevel;
    private Boolean isTimed;
    private Integer timerMin;
    private BigDecimal passPercent;
    private Boolean roundScoreUp;
    private String status;
    private String createdBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Long questionCount;
    private Long maxScore;
}
