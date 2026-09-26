package ee.testiplatvorm.service;


import ee.testiplatvorm.controller.user.dto.UserResponse;
import ee.testiplatvorm.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponse> findAllUsers() {
        List<UserResponse> allUserResponses = userRepository.findAllUserResponses();
        return allUserResponses;


    }
}
