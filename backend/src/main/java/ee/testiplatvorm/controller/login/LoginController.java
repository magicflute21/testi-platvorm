package ee.testiplatvorm.controller.login;


import ee.testiplatvorm.controller.login.dto.LoginRequest;
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
    public void login(@RequestBody LoginRequest loginRequest) {
        loginService.login(loginRequest);
    }

}
