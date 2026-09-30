package ee.testiplatvorm.controller.question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link ee.testiplatvorm.persistence.questionanswer.QuestionAnswer}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerDto {
    private Integer questionAnswerId;
    private String answerText;
    private Boolean correctChoice;
}