package ee.testiplatvorm.persistence.questionanswer;

import ee.testiplatvorm.controller.testattempt.dto.TestAttemptAnswerDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionAnswerMapper {
   @Mapping(source ="id", target="questionAnswerId")
   @Mapping(source ="answerText", target="answerText")
   TestAttemptAnswerDto toQuestionAnswerDto(QuestionAnswer questionAnswer);

   List<TestAttemptAnswerDto> toQuestionAnswerDtos(List<QuestionAnswer> questionAnswers);
}