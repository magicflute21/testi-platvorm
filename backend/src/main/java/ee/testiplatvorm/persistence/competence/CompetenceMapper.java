package ee.testiplatvorm.persistence.competence;

import ee.testiplatvorm.controller.competence.dto.CompetenceResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompetenceMapper {
    @Mapping(source = "id", target = "competenceId")
    @Mapping(source = "name", target = "competenceName")
    CompetenceResponseDto toCompetenceResponseDto(Competence competence);

    List<CompetenceResponseDto> toCompetenceResponseDtos(List<Competence> competences);
}
