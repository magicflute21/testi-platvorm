package ee.testiplatvorm.service;


import ee.testiplatvorm.Status;
import ee.testiplatvorm.controller.user.dto.NewUserRequest;
import ee.testiplatvorm.controller.user.dto.UserResponse;
import ee.testiplatvorm.infrastructure.exception.ForbiddenException;
import ee.testiplatvorm.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.testiplatvorm.persistence.Profile;
import ee.testiplatvorm.persistence.ProfileRepository;
import ee.testiplatvorm.persistence.Role;
import ee.testiplatvorm.persistence.group.Group;
import ee.testiplatvorm.persistence.groupmember.GroupMember;
import ee.testiplatvorm.persistence.groupmember.GroupMemberRepository;
import ee.testiplatvorm.persistence.user.User;
import ee.testiplatvorm.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static ee.testiplatvorm.Error.EMAIL_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ProfileRepository profileRepository;
    private final RoleService roleService;
    private final GroupService groupService;
    private final CurrentUserService currentUserService;

    public List<UserResponse> findAllUsers() {
        List<UserResponse> allUserResponses = userRepository.findAllUserResponses();
        List<GroupMember> groupMembers = groupMemberRepository.findAllGroupMembers();
        handleAddGroupNames(allUserResponses, groupMembers);
        return allUserResponses;
    }

    @Transactional
    public void addUser(NewUserRequest newUserRequest) {
        String email = newUserRequest.getEmail().trim().toLowerCase();
        validateEmailIsAvailable(email);
        Role role = roleService.getValidRoleBy(newUserRequest.getRoleId());
        Instant now = Instant.now();

        User user = createUser(email, newUserRequest.getPassword(), role, now);
        userRepository.save(user);

        Profile profile = createProfile(user, newUserRequest.getFirstName().trim(), newUserRequest.getLastName().trim(), now);
        profileRepository.save(profile);

        handleAddGroupMembers(user, newUserRequest.getGroupIds(), now);
    }

    public void updateUserStatus(Integer userId, String status) {
        User user = getValidUserBy(userId);
        user.setStatus(status);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    public void deleteUser(Integer userId) {
        User user = getValidUserBy(userId);
        String code = Status.STATUS_INACTIVE.getCode();
        user.setStatus(code);
        Instant now = Instant.now();
        user.setUpdatedAt(now);
        userRepository.save(user);
    }

    public User getValidUserBy(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new PrimaryKeyNotFoundException("userId", userId));
    }

    private void validateEmailIsAvailable(String email) {
        if (userRepository.existsUserBy(email)) {
            throw new ForbiddenException(EMAIL_ALREADY_EXISTS.getMessage(), EMAIL_ALREADY_EXISTS.name());
        }
    }

    private User createUser(String email, String password, Role role, Instant now) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setRole(role);
        user.setStatus(Status.STATUS_ACTIVE.getCode());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    private Profile createProfile(User user, String firstName, String lastName, Instant now) {
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setCreatedAt(now);
        profile.setUpdatedAt(now);
        return profile;
    }

    private void handleAddGroupMembers(User user, List<Integer> groupIds, Instant now) {
        if (groupIds.isEmpty()) {
            return;
        }
        User addedBy = getValidUserBy(currentUserService.getUserId());
        for (Integer groupId : groupIds.stream().distinct().toList()) {
            Group group = groupService.getValidGroupBy(groupId);
            GroupMember groupMember = createGroupMember(group, user, addedBy, now);
            groupMemberRepository.save(groupMember);
        }
    }

    private GroupMember createGroupMember(Group group, User user, User addedBy, Instant now) {
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMember.setAddedBy(addedBy);
        groupMember.setCreatedAt(now);
        groupMember.setUpdatedAt(now);
        return groupMember;
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
