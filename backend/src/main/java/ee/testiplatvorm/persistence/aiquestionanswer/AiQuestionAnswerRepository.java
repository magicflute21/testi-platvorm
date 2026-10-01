package ee.testiplatvorm.persistence.aiquestionanswer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AiQuestionAnswerRepository extends JpaRepository<AiQuestionAnswer, Integer> {
    @Query("select a from AiQuestionAnswer a where a.aiQuestion.id = :aiQuestionId order by a.id")
    List<AiQuestionAnswer> findAnswersBy(Integer aiQuestionId);
}
