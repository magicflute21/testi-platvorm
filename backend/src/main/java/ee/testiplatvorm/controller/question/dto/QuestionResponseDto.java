package ee.testiplatvorm.controller.question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDto {
    private Integer questionId;
    private String questionTitle;
}
