package ee.testiplatvorm.persistence.questionanswer;

import ee.testiplatvorm.controller.testattempt.dto.TestAttemptAnswerDto;
import ee.testiplatvorm.controller.question.dto.QuestionAnswerDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionAnswerMapper {
    @Mapping(source = "id", target = "questionAnswerId")
    @Mapping(source = "answerText", target = "answerText")
    TestAttemptAnswerDto toQuestionAnswerDto(QuestionAnswer questionAnswer);

    List<TestAttemptAnswerDto> toQuestionAnswerDtos(List<QuestionAnswer> questionAnswers);

    QuestionAnswer toEntity(QuestionAnswerDto questionAnswerDto);

    QuestionAnswerDto toDto(QuestionAnswer questionAnswer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    QuestionAnswer partialUpdate(QuestionAnswerDto questionAnswerDto, @MappingTarget QuestionAnswer questionAnswer);
}