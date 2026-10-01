package ee.testiplatvorm.controller.aiquestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link ee.testiplatvorm.persistence.aiquestionanswer.AiQuestionAnswer}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiQuestionBankAnswerDto {
    private Integer aiQuestionAnswerId;
    private String answerText;
    private Boolean correctChoice;
}
