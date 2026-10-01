package ee.testiplatvorm.controller.question.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreateAnswerRequestDto {

    private String answerText;
    private Boolean isCorrect;

}
