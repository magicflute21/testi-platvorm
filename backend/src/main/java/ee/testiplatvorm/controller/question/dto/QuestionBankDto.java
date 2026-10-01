package ee.testiplatvorm.controller.question.dto;

import ee.testiplatvorm.persistence.question.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for {@link Question}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBankDto {
    private Integer questionId;
    private String questionTitle;
    private String questionDescription;
    private String questionTypeName;
    private Integer competenceId;
    private String competenceName;
    private String questionStatus;
    private List<QuestionBankAnswerDto> answers;
}