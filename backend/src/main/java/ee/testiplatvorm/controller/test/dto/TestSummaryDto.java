package ee.testiplatvorm.controller.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link ee.testiplatvorm.persistence.test.Test}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSummaryDto implements Serializable {
    private Integer testId;
    private String testName;
    private String testShortDescription;
    private String testStatus;
}