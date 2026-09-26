package ee.testiplatvorm.controller.test.dto;

import lombok.Data;

@Data
public class TestStartDto {
    private Integer testId;
    private String title;
    private String description;
    private String shortDescription;
    private String competence;
    private String competenceLevel;
    private Boolean isTimed;
    private Integer timerMin;
    private String status;
}
