package ee.testiplatvorm.controller.level.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelDto {
    private Integer levelId;
    private Integer level;
    private String levelName;
    private String description;
}
