package ee.testiplatvorm.controller.questiontype.dto;

import ee.testiplatvorm.persistence.questiontype.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link QuestionType}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionTypeDto implements Serializable {
    Integer questionTypeId;
    String questionTypeName;
}