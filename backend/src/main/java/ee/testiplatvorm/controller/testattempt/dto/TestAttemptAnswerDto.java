package ee.testiplatvorm.controller.testattempt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestAttemptAnswerDto {
    private Integer questionAnswerId;
    private String answerText;
}
