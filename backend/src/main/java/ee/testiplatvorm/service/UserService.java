package ee.testiplatvorm.service;


import ee.testiplatvorm.controller.user.dto.UserResponse;
import ee.testiplatvorm.persistence.groupmember.GroupMember;
import ee.testiplatvorm.persistence.groupmember.GroupMemberRepository;
import ee.testiplatvorm.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    public List<UserResponse> findAllUsers() {
        List<UserResponse> allUserResponses = userRepository.findAllUserResponses();
        List<GroupMember> groupMembers = groupMemberRepository.findAllGroupMembers();
        for (UserResponse userResponse : allUserResponses) {
            for (GroupMember groupMember : groupMembers) {
                if (userResponse.getUserId().equals(groupMember.getUser().getId())) {
                    userResponse.getGroupNames().add(groupMember.getGroup().getName());
                }
            }

        }


        return allUserResponses;
    }


}
