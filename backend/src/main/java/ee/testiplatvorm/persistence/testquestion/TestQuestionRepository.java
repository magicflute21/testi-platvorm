package ee.testiplatvorm.persistence.testquestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Integer> {
    @Query("select t from TestQuestion t where t.test.id = :testId order by t.position")
    List<TestQuestion> findQuestionsBy(Integer testId);

}