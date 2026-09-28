package ee.testiplatvorm.service;


import ee.testiplatvorm.controller.usertest.dto.UserTestSummaryDto;
import ee.testiplatvorm.persistence.usertest.UserTest;
import ee.testiplatvorm.persistence.usertest.UserTestMapper;
import ee.testiplatvorm.persistence.usertest.UserTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static ee.testiplatvorm.Status.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor


public class UserTestService {

    private final CurrentUserService currentUserService;
    private final UserTestRepository userTestRepository;
    private final UserTestMapper userTestMapper;

    public List<UserTestSummaryDto> findUserTests() {
        Integer userId = currentUserService.getUserId();
        List<UserTest> userTests = userTestRepository.findUserTestsBy(STATUS_ACTIVE.getCode(), userId);
        List<UserTestSummaryDto> userTestSummaryDtos = userTestMapper.toUserTestSummaryDtos(userTests);
        return userTestSummaryDtos;


    }

}
