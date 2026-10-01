package ee.testiplatvorm.persistence.questionanswer;

import ee.testiplatvorm.controller.question.dto.QuestionCreateAnswerRequestDto;
import ee.testiplatvorm.controller.question.dto.QuestionCreateAnswerRequestDto;
import ee.testiplatvorm.controller.testattempt.dto.TestAttemptAnswerDto;
import ee.testiplatvorm.controller.question.dto.QuestionBankAnswerDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionAnswerMapper {
    @Mapping(source = "id", target = "questionAnswerId")
    @Mapping(source = "answerText", target = "answerText")
    TestAttemptAnswerDto toQuestionAnswerDto(QuestionAnswer questionAnswer);

    List<TestAttemptAnswerDto> toQuestionAnswerDtos(List<QuestionAnswer> questionAnswers);

    @Mapping(source = "id", target = "questionAnswerId")
    @Mapping(source = "answerText", target = "answerText")
    @Mapping(source = "correctChoice", target = "correctChoice")
    QuestionBankAnswerDto toQuestionBankAnswerDto(QuestionAnswer questionAnswer);

    List<QuestionBankAnswerDto> toQuestionBankAnswerDtos(List<QuestionAnswer> questionAnswers);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "question")
    @Mapping(source = "answerText", target = "answerText")
    @Mapping(source = "isCorrect", target = "correctChoice")
    @Mapping(ignore = true, target = "correctPosition")
    @Mapping(ignore = true, target = "pairedAnswer")
    @Mapping(ignore = true, target = "status")
    QuestionAnswer toCreateQuestionAnswer(QuestionCreateAnswerRequestDto questionCreateAnswerRequestDto);

    List<QuestionAnswer> toCreateQuestionAnswers(List<QuestionCreateAnswerRequestDto> questionCreateAnswerRequestDtos);

}