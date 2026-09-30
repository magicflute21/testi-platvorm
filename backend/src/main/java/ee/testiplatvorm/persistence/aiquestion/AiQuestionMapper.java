package ee.testiplatvorm.persistence.aiquestion;

import ee.testiplatvorm.controller.aiquestion.dto.AiQuestionDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AiQuestionMapper {
    @Mapping(source = "competenceLevelId", target = "competenceLevel.id")
    @Mapping(source = "competenceId", target = "competence.id")
    AiQuestion toEntity(AiQuestionDto aiQuestionDto);

    @InheritInverseConfiguration(name = "toEntity")
    AiQuestionDto toDto(AiQuestion aiQuestion);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AiQuestion partialUpdate(AiQuestionDto aiQuestionDto, @MappingTarget AiQuestion aiQuestion);
}