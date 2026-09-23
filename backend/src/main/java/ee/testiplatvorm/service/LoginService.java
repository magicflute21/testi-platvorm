package ee.testiplatvorm.service;


import ee.testiplatvorm.controller.login.dto.LoginRequest;
import ee.testiplatvorm.persistence.User;
import ee.testiplatvorm.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    public void login(LoginRequest loginRequest) {
        User user = userRepository.findUserBy(loginRequest.getEmail(), loginRequest.getPassword())
                .orElseThrow();


    }
}
