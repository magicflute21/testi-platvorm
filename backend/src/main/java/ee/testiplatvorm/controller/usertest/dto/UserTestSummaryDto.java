package ee.testiplatvorm.controller.usertest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTestSummaryDto implements Serializable {
    private Integer userTestId;
    private Integer testId;
    private String testName;
    private String testShortDescription;
    private String userTestStatus;
}