package ee.testiplatvorm.controller.aiquestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO for {@link ee.testiplatvorm.persistence.aiquestion.AiQuestion}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiQuestionBankDto {
    private Integer aiQuestionId;
    private String questionTitle;
    private String questionDescription;
    private String questionTypeName;
    private Integer competenceId;
    private String competenceName;
    private String competenceLevelName;
    private Integer score;
    private String aiQuestionStatus;
    private String feedback;
    private OffsetDateTime createdAt;
    private List<AiQuestionBankAnswerDto> answers;
}
