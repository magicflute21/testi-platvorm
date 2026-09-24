package ee.testiplatvorm.persistence.competence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetenceRepository extends JpaRepository<Competence, Integer> {
    @Query("select c from Competence c where c.name = :name and c.status = :status")
    List<Competence> findCompetencesBy( String name, String status);


}