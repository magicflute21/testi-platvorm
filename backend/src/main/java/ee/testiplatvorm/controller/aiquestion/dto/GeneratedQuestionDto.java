package ee.testiplatvorm.controller.aiquestion.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeneratedQuestionDto {
    // Täidetud ainult siis, kui AI ei suutnud kompetentsi/taset ära tunda - siis on muud väljad tühjad
    private String clarifyingQuestion;

    private String title;
    private String description;
    private List<GeneratedAnswerDto> answers;
    private Integer competenceLevelId;
    private String competenceName;
    private String levelName;
    private Integer questionTypeId;
    private String questionTypeName;
}
