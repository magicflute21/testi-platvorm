package ee.testiplatvorm.persistence.question;

import ee.testiplatvorm.controller.question.dto.QuestionCreateRequestDto;
import ee.testiplatvorm.controller.question.dto.QuestionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionMapper {
    @Mapping(source = "id", target = "questionId")
    @Mapping(source = "title", target = "questionTitle")
    QuestionResponseDto toQuestionResponseDto(Question question);

    List<QuestionResponseDto> toQuestionResponseDtos(List<Question> questions);


    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "competence")
    @Mapping(ignore = true, target = "questionType")
    @Mapping(ignore = true, target = "competenceLevel")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "score", target = "score")
    @Mapping(ignore = true, target = "status")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedAt")
    Question toQuestion(QuestionCreateRequestDto questionCreateRequestDto);


}