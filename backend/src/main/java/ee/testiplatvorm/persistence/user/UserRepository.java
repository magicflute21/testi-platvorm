package ee.testiplatvorm.persistence.user;

import ee.testiplatvorm.controller.user.dto.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select u from User u where u.email = :email and u.passwordHash = :passwordHash and u.status = :status")
    Optional<User> findUserBy(String email, String passwordHash, String status);

    @Query("""
            select new  ee.testiplatvorm.controller.user.dto.UserResponse (u.id, p.firstName, p.lastName, u.email, u.role.name, u.status)
                       from User u left join Profile p on u.id = p.user.id """)
    List<UserResponse> findAllUserResponses();

}