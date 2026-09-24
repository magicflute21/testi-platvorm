package ee.testiplatvorm.controller.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceResponseDto {
    private Integer competenceId;
    private String competenceName;

}
