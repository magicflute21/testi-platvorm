package ee.testiplatvorm.controller.user;


import ee.testiplatvorm.controller.user.dto.UserResponse;
import ee.testiplatvorm.persistence.user.UserMapper;
import ee.testiplatvorm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor


public class UserController {

    private final UserService userService;

    @GetMapping("/api/users")
    public List<UserResponse> findAllUsers() {

        List<UserResponse> allUsersResponses = userService.findAllUsers();
        return allUsersResponses;
    }

    @DeleteMapping("/api/users/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }


}


