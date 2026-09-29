package ee.testiplatvorm.persistence.competencelevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetenceLevelRepository extends JpaRepository<CompetenceLevel, Integer> {
    @Query("""
            select c from CompetenceLevel c
            where c.competence.id = :competenceId and c.status = :status
            order by c.level.level ASC""")
    List<CompetenceLevel> findCompetenceLevelsBy(Integer competenceId, String status);


}