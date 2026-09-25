package ee.testiplatvorm.persistence.testquestion;

import ee.testiplatvorm.controller.testattempt.dto.TestAttemptQuestionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TestQuestionMapper {

    @Mapping(source = "question.id", target = "questionId")
    @Mapping(source = "position", target = "position")
    @Mapping(source = "question.title", target = "title")
    @Mapping(source = "question.description", target = "description")
    @Mapping(source = "question.questionType.name", target = "questionTypeName")
    @Mapping(ignore = true, target = "answers")
    @Mapping(ignore = true, target = "selectedQuestionAnswerIds")
    TestAttemptQuestionDto toTestAttemptQuestionDto(TestQuestion testQuestion);

    List<TestAttemptQuestionDto> toTestAttemptQuestionDtos(List<TestQuestion> testQuestions);
}