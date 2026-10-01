package ee.testiplatvorm.persistence.result;

import ee.testiplatvorm.controller.result.dto.ResultResponseDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ResultMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "userTest.id", target = "userTestId")
    @Mapping(source = "userTest.opensAt", target = "userTestOpensAt")
    @Mapping(source = "userTest.createdAt", target = "userTestCreatedAt")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "maxScore", target = "maxScore")
    @Mapping(source = "scoreTotal", target = "scoreTotal")
    @Mapping(source = "completedAt", target = "completedAt")
    @Mapping(source = "startedAt", target = "startedAt")
    @Mapping(source = "totalQuestions", target = "totalQuestions")
    @Mapping(source = "questionsAnswered", target = "questionsAnswered")
    @Mapping(ignore = true, target = "userAchievedScorePercentage")
    ResultResponseDto toResultResponseDto(Result result);
}