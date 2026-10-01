package ee.testiplatvorm.persistence.aiquestion;

import ee.testiplatvorm.controller.aiquestion.dto.AiQuestionBankDto;
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

    @Mapping(source = "id", target = "aiQuestionId")
    @Mapping(source = "title", target = "questionTitle")
    @Mapping(source = "description", target = "questionDescription")
    @Mapping(source = "competence.id", target = "competenceId")
    @Mapping(source = "competence.name", target = "competenceName")
    @Mapping(source = "competenceLevel.level.name", target = "competenceLevelName")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "status", target = "aiQuestionStatus")
    @Mapping(source = "feedback", target = "feedback")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(ignore = true, target = "questionTypeName")
    @Mapping(ignore = true, target = "answers")
    AiQuestionBankDto toAiQuestionBankDto(AiQuestion aiQuestion);
}