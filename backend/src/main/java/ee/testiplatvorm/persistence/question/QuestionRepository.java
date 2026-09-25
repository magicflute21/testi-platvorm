package ee.testiplatvorm.persistence.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query("select q from Question q where q.id = :id and q.status = :status")
    List<Question> findQuestionsBy(Integer id, String status);

}