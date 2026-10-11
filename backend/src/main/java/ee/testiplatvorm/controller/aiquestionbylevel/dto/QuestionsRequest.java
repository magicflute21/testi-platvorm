package ee.testiplatvorm.controller.aiquestionbylevel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsRequest {
    private Integer competenceLevelId;
    private Integer questionTypeId;
    private Integer questionCount;
    private String instructions;
}
