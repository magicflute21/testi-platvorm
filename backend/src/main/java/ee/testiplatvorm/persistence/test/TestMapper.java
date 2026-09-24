package ee.testiplatvorm.persistence.test;

import ee.testiplatvorm.controller.test.dto.TestSummaryDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TestMapper {

    @Mapping(source = "id", target = "testId")
    @Mapping(source = "name", target = "testName")
    @Mapping(source = "shortDescription", target = "testShortDescription")
    @Mapping(source = "status", target = "testStatus")
    TestSummaryDto toTestSummaryDto(Test tests);

    List<TestSummaryDto> toTestSummaryDtos (List <Test> tests);
}