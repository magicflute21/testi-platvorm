package ee.testiplatvorm.persistence.group;

import ee.testiplatvorm.controller.group.dto.GroupResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    @Query("select new ee.testiplatvorm.controller.group.dto.GroupResponse(g.id, g.name) from Group g where g.status = :status order by g.name")
    List<GroupResponse> findGroupResponsesBy(String status);

}
