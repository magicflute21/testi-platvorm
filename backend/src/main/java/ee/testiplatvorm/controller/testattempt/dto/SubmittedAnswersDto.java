package ee.testiplatvorm.controller.testattempt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedAnswersDto {
    private Integer questionId;
    private List<Integer> answerIds;
}
