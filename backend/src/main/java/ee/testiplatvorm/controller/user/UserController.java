package ee.testiplatvorm.controller.user;


import ee.testiplatvorm.controller.login.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor


public class UserController {


    private final UserService userService;
}