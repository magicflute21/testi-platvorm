package ee.testiplatvorm.persistence.questiontype;

import ee.testiplatvorm.controller.questiontype.dto.QuestionTypeDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionTypeMapper {


    @Mapping(source = "id", target = "questionTypeId")
    @Mapping(source = "name", target = "questionTypeName")
    QuestionTypeDto toQuestionTypeDto(QuestionType questionType);

    List<QuestionTypeDto> toQuestionTypeDtos(List<QuestionType> questionTypes);

}