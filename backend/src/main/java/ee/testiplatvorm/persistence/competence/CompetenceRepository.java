package ee.testiplatvorm.persistence.competence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetenceRepository extends JpaRepository<Competence, Integer> {
    @Query("select c from Competence c where c.status = :status order by c.name")
    List<Competence> findCompetencesBy(String status);
}
