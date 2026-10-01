package ee.testiplatvorm.persistence.question;

import ee.testiplatvorm.controller.question.dto.QuestionBankDto;
import ee.testiplatvorm.controller.question.dto.QuestionResponseDto;
import ee.testiplatvorm.controller.question.dto.QuestionUpdateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionMapper {
    @Mapping(source = "id", target = "questionId")
    @Mapping(source = "title", target = "questionTitle")
    QuestionResponseDto toQuestionResponseDto(Question question);

    List<QuestionResponseDto> toQuestionResponseDtos(List<Question> questions);

    @Mapping(source = "id", target = "questionId")
    @Mapping(source = "title", target = "questionTitle")
    @Mapping(source = "description", target = "questionDescription")
    @Mapping(source = "questionType.name", target = "questionTypeName")
    @Mapping(source = "competence.id", target = "competenceId")
    @Mapping(source = "competence.name", target = "competenceName")
    @Mapping(source = "competenceLevel.level.name", target = "competenceLevelName")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "status", target = "questionStatus")
    @Mapping(ignore = true, target = "answers")
    QuestionBankDto toQuestionBankDto(Question question);

    List<QuestionBankDto> toQuestionBankDtos(List<Question> questions);

    @Mapping(source = "questionTitle", target = "title")
    @Mapping(source = "questionDescription", target = "description")
    @Mapping(source = "questionStatus", target = "status")
    void updateQuestion(QuestionUpdateRequestDto questionUpdateRequestDto, @MappingTarget Question question);
}