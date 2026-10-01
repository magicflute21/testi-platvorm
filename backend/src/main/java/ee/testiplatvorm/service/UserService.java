package ee.testiplatvorm.service;


import ee.testiplatvorm.Status;
import ee.testiplatvorm.controller.user.dto.UserResponse;
import ee.testiplatvorm.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.testiplatvorm.persistence.groupmember.GroupMember;
import ee.testiplatvorm.persistence.groupmember.GroupMemberRepository;
import ee.testiplatvorm.persistence.user.User;
import ee.testiplatvorm.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    public List<UserResponse> findAllUsers() {
        List<UserResponse> allUserResponses = userRepository.findAllUserResponses();
        List<GroupMember> groupMembers = groupMemberRepository.findAllGroupMembers();
        handleAddGroupNames(allUserResponses, groupMembers);
        return allUserResponses;
    }

    public void deleteUser(Integer userId) {
        User user = getValidUserBy(userId);
        String code = Status.STATUS_INACTIVE.getCode();
        user.setStatus(code);
        OffsetDateTime now = OffsetDateTime.now();
        user.setUpdatedAt(now);
        userRepository.save(user);
    }

    public User getValidUserBy(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new PrimaryKeyNotFoundException("userId", userId));
    }

    private void handleAddGroupNames(List<UserResponse> allUserResponses, List<GroupMember> groupMembers) {
        for (UserResponse userResponse : allUserResponses) {
            for (GroupMember groupMember : groupMembers) {
                if (userResponse.getUserId().equals(groupMember.getUser().getId())) {
                    userResponse.getGroupNames().add(groupMember.getGroup().getName());
                }
            }

        }
    }
}
