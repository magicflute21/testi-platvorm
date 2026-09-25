package ee.testiplatvorm.controller.testattempt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestAttemptQuestionDto {
    private Integer questionId;
    private Integer position;
    private String title;
    private String description;
    private String questionTypeName;
    private List<TestAttemptAnswerDto> answers;
    private List<Integer> selectedQuestionAnswerIds;
}
