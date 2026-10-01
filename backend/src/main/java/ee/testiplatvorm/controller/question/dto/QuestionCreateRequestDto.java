package ee.testiplatvorm.controller.question.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCreateRequestDto {

    private Integer competenceLevelId;
    private String title;
    private String description;
    private Integer questionTypeId;
    private Integer score;
    private List<QuestionCreateAnswerRequestDto> answers;
}
