package ee.testiplatvorm.controller.user;


import ee.testiplatvorm.controller.user.dto.UserResponse;
import ee.testiplatvorm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor


public class UserController {

    private final UserService userService;

    @GetMapping("/api/users")
    public List<UserResponse> findAllUsers() {

        List<UserResponse> allUsers = userService.findAllUsers();
        return  allUsers;
    }


}


