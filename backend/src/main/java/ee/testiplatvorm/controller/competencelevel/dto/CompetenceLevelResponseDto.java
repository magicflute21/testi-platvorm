package ee.testiplatvorm.controller.competencelevel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceLevelResponseDto {

    private Integer competenceLevelId;
    private String levelName;

}