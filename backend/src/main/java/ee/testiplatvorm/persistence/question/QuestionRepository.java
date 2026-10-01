package ee.testiplatvorm.persistence.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query("select q from Question q where q.competenceLevel.id = :competenceLevelId and q.status = :status order by q.title")
    List<Question> findQuestionsBy(Integer competenceLevelId, String status);

    @Query("""
            select q from Question q
            where (:competenceId is null or q.competence.id = :competenceId)
            order by q.id""")
    List<Question> findAllQuestionsBy(Integer competenceId);
}