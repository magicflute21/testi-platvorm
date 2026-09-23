package ee.testiplatvorm.controller.login;


import ee.testiplatvorm.controller.login.dto.LoginRequest;
import ee.testiplatvorm.controller.login.dto.LoginResponse;
import ee.testiplatvorm.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class LoginController {

    private final LoginService loginService;

    @PostMapping("/api/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse =  loginService.login(loginRequest);
        System.out.println(loginResponse);
        return loginResponse;
    }
}
