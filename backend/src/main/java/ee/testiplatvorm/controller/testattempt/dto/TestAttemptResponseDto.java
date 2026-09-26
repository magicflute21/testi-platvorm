package ee.testiplatvorm.controller.testattempt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestAttemptResponseDto {
    private Integer userTestId;
    private Integer testId;
    private String testName;
    private String testShortDescription;
    private String testDescription;
    private Boolean isTimed;
    private Integer timerMin;

    private List<TestAttemptQuestionDto> questions;
}
