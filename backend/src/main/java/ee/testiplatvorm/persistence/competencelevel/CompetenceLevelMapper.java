package ee.testiplatvorm.persistence.competencelevel;

import ee.testiplatvorm.controller.competencelevel.dto.CompetenceLevelResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompetenceLevelMapper {
    @Mapping(source = "id", target = "competenceLevelId")
    @Mapping(source = "level.name", target = "levelName")
    CompetenceLevelResponseDto toCompetenceLevelResponseDto(CompetenceLevel competenceLevel);


    List<CompetenceLevelResponseDto> toCompetenceLevelResponseDtos(List<CompetenceLevel> competenceLevels);
}