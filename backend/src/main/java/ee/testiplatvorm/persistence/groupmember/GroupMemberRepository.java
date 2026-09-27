package ee.testiplatvorm.persistence.groupmember;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<User, Integer> {
}
