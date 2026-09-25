package ee.testiplatvorm.persistence.questionanswer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Integer> {
    @Query("select q from QuestionAnswer q where q.question.id = :questionId and q.status = :status")
    List<QuestionAnswer> findAnswersBy(Integer questionId,  String status);
}