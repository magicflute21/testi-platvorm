package ee.testiplatvorm.controller.aiquestion.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiQuestionGenerationResponse {
    // Täidetud ainult siis, kui AI ei suutnud kompetentsi/taset ära tunda - siis on questions tühi
    private String clarifyingQuestion;
    private List<GeneratedQuestionDto> questions = new ArrayList<>();
    // Mitu küsimust saab kasutaja sel tunnil veel genereerida
    private Integer remainingQuestionCount;
}
