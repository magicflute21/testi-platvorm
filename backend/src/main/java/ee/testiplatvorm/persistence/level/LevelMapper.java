package ee.testiplatvorm.persistence.level;

import ee.testiplatvorm.controller.level.dto.LevelDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface LevelMapper {
    @Mapping(source = "id", target = "levelId")
    @Mapping(source = "name", target = "levelName")
    LevelDto toLevelDto(Level level);

    List<LevelDto> toLevelDtos(List<Level> levels);
}
