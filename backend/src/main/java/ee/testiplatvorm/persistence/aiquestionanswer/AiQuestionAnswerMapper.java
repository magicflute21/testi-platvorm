package ee.testiplatvorm.persistence.aiquestionanswer;

import ee.testiplatvorm.controller.aiquestion.dto.AiQuestionBankAnswerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AiQuestionAnswerMapper {
    @Mapping(source = "id", target = "aiQuestionAnswerId")
    @Mapping(source = "answerText", target = "answerText")
    @Mapping(source = "isCorrect", target = "correctChoice")
    AiQuestionBankAnswerDto toAiQuestionBankAnswerDto(AiQuestionAnswer aiQuestionAnswer);

    List<AiQuestionBankAnswerDto> toAiQuestionBankAnswerDtos(List<AiQuestionAnswer> aiQuestionAnswers);
}
