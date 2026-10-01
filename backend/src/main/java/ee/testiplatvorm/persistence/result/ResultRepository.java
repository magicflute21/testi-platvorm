package ee.testiplatvorm.persistence.result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Integer> {
    @Query("select r from Result r where r.userTest.id = :userTestId")
    Optional<Result> findResultByUserTestId(Integer userTestId);
}