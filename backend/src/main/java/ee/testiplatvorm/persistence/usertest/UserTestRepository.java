package ee.testiplatvorm.persistence.usertest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserTestRepository extends JpaRepository<UserTest, Integer> {
    @Query("""
            select u from UserTest u
            where u.user.id = :userId and u.test.id = :testId and u.status = :userTestStatus and u.test.status = :testStatus and u.user.status = :userStatus""")
    Optional<UserTest> getValidUserTestBy(Integer userId, Integer testId, String userTestStatus, String testStatus, String userStatus);


    @Query("""
            select u from UserTest u
            where u.user.id = :userId and u.test.status = :testStatus
            order by u.status DESC, u.closesAt ASC""")
    List<UserTest> findUserTestsBy(Integer userId, String testStatus);

    @Query("select count(u) from UserTest u where u.user.id = :userId and u.status = :userTestStatus and u.test.status = :testStatus")
    Long countUserTestsBy(Integer userId, String userTestStatus, String testStatus);

}