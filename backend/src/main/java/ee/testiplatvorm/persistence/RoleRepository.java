package ee.testiplatvorm.persistence;

import ee.testiplatvorm.controller.role.dto.RoleResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("select new ee.testiplatvorm.controller.role.dto.RoleResponse(r.id, r.name) from Role r order by r.id")
    List<RoleResponse> findAllRoleResponses();

}
