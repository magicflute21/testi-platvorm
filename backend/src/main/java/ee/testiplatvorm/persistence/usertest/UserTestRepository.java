package ee.testiplatvorm.persistence.usertest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserTestRepository extends JpaRepository<UserTest, Integer> {
    @Query("""
            select u from UserTest u
            where u.user.id = :userId and u.test.id = :testId and u.status = :userTestStatus and u.test.status = :testStatus and u.user.status = :userStatus""")
    Optional<UserTest> getValidUserTestBy(Integer userId, Integer testId, String userTestStatus, String testStatus, String userStatus);



}