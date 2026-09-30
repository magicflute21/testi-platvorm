package ee.testiplatvorm.persistence.question;

import ee.testiplatvorm.controller.question.dto.QuestionResponseDto;
import ee.testiplatvorm.service.AllQuestionsResponseDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionMapper {
    @Mapping(source = "id", target = "questionId")
    @Mapping(source = "title", target = "questionTitle")
    QuestionResponseDto toQuestionResponseDto(Question question);

    List<QuestionResponseDto> toQuestionResponseDtos(List<Question> questions);

    @Mapping(source = "competenceStatus", target = "competence.status")
    @Mapping(source = "competenceName", target = "competence.name")
    @Mapping(source = "competenceId", target = "competence.id")
    Question toEntity(AllQuestionsResponseDto allQuestionsResponseDto);

    @InheritInverseConfiguration(name = "toEntity")
    AllQuestionsResponseDto toDto(Question question);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Question partialUpdate(AllQuestionsResponseDto allQuestionsResponseDto, @MappingTarget Question question);
}