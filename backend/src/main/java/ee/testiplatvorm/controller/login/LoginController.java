package ee.testiplatvorm.controller.login;

import ee.testiplatvorm.controller.login.dto.LoginRequest;
import ee.testiplatvorm.controller.login.dto.LoginResponse;
import ee.testiplatvorm.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class LoginController {

    private final LoginService loginService;

    @PostMapping("/api/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        LoginResponse loginResponse =  loginService.login(loginRequest);

        request.getSession(true);
        request.changeSessionId();
        request.getSession().setAttribute("userId", loginResponse.getUserId());

        return loginResponse;
    }
}
