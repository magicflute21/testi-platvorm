package ee.testiplatvorm.persistence.aiquestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AiQuestionRepository extends JpaRepository<AiQuestion, Integer> {
    @Query("""
            select a from AiQuestion a
            where (:status is null or a.status = :status)
            and (:competenceId is null or a.competence.id = :competenceId)
            order by a.createdAt desc, a.id desc""")
    List<AiQuestion> findAiQuestionsBy(String status, Integer competenceId);
}