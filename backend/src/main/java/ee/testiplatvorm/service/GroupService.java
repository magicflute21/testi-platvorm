package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.group.dto.GroupResponse;
import ee.testiplatvorm.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.testiplatvorm.persistence.group.Group;
import ee.testiplatvorm.persistence.group.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static ee.testiplatvorm.Status.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    public List<GroupResponse> findActiveGroups() {
        return groupRepository.findGroupResponsesBy(STATUS_ACTIVE.getCode());
    }

    public Group getValidGroupBy(Integer groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new PrimaryKeyNotFoundException("groupId", groupId));
    }
}
