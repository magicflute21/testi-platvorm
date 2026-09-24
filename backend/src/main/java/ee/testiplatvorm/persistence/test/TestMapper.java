package ee.testiplatvorm.persistence.test;

import ee.testiplatvorm.controller.test.dto.TestSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TestMapper {

    @Mapping(source = "id", target = "testId")
    @Mapping(source = "name", target = "testName")
    @Mapping(source = "shortDescription", target = "testShortDescription")
    @Mapping(source = "status", target = "testStatus")
    TestSummaryDto toTestSummaryDto(Test test);

    List<TestSummaryDto> toTestSummaryDtos(List<Test> tests);
}