package ee.testiplatvorm.persistence.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query("select q from Question q where q.competenceLevel.id = :competenceLevelId and q.status = :status order by q.title")
    List<Question> findQuestionsBy(Integer competenceLevelId, String status);

    @Query("""
            select q from Question q
            where q.competence.id = :id
            order by q.competence.id""")
    List<Question> findAllQuestionsBy(Integer id);
}