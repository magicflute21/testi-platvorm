package ee.testiplatvorm.controller.question.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCreateRequestDto {

    private Integer competenceLevelId;
    private String questionTitle;
    private String questionDescription;
    private Integer questionTypeId;
    private Integer questionScore;
}
