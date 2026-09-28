package ee.testiplatvorm.persistence.usertest;

import ee.testiplatvorm.controller.testattempt.dto.TestAttemptResponseDto;
import ee.testiplatvorm.controller.usertest.dto.UserTestSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserTestMapper {
    @Mapping(source = "id", target = "userTestId")
    @Mapping(source = "test.id", target = "testId")
    @Mapping(source = "test.name", target = "testName")
    @Mapping(source = "test.description", target = "testDescription")
    @Mapping(source = "test.shortDescription", target = "testShortDescription")
    @Mapping(source = "test.isTimed", target = "isTimed")
    @Mapping(source = "test.timerMin", target = "timerMin")
    @Mapping(ignore = true, target = "questions")
    TestAttemptResponseDto toTestAttemptResponseDto(UserTest userTest);

    @Mapping(source = "id", target = "userTestId")
    @Mapping(source = "test.shortDescription", target = "testShortDescription")
    @Mapping(source = "status", target = "userTestStatus")
    @Mapping(source = "test.name", target = "testName")
    @Mapping(source = "test.id", target = "testId")
    UserTestSummaryDto toUserTestSummaryDto(UserTest userTest);

    List<UserTestSummaryDto> toUserTestSummaryDtos(List<UserTest> userTests);


}