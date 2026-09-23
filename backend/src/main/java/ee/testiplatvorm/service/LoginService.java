package ee.testiplatvorm.service;


import ee.testiplatvorm.controller.login.dto.LoginRequest;
import ee.testiplatvorm.controller.login.dto.LoginResponse;
import ee.testiplatvorm.infrastructure.exception.ForbiddenException;
import ee.testiplatvorm.persistence.user.User;
import ee.testiplatvorm.persistence.user.UserMapper;
import ee.testiplatvorm.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static ee.testiplatvorm.Error.INCORRECT_CREDENTIALS;
import static ee.testiplatvorm.Status.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findUserBy(loginRequest.getEmail(), loginRequest.getPassword(), STATUS_ACTIVE.getCode())
                .orElseThrow(() -> new ForbiddenException(INCORRECT_CREDENTIALS.getMessage(), INCORRECT_CREDENTIALS.name()));

        return userMapper.toLoginResponse(user);
    }
}
