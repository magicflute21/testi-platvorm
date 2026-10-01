package ee.testiplatvorm.controller.competence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceDto {
    private Integer competenceId;
    private String competenceName;
    private String shortDescription;
    private String description;
    private String docUrl;
    private String docFilename;
    private String status;
    private String createdByEmail;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
