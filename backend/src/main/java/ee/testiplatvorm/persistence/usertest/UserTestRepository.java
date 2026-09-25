package ee.testiplatvorm.persistence.usertest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserTestRepository extends JpaRepository<UserTest, Integer> {
    @Query("select u from UserTest u where u.id = :userTestId and u.status = :userTestStatus and u.test.status = :testStatus")
    Optional<UserTest> getValidUserTestBy(Integer userTestId, String userTestStatus, String testStatus);

}