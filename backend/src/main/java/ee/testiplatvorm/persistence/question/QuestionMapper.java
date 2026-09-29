package ee.testiplatvorm.persistence.question;

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
}