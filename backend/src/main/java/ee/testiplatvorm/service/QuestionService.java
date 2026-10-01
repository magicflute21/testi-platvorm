package ee.testiplatvorm.service;

import ee.testiplatvorm.Error;
import ee.testiplatvorm.controller.question.dto.QuestionCreateRequestDto;
import ee.testiplatvorm.controller.question.dto.QuestionResponseDto;
import ee.testiplatvorm.infrastructure.exception.ForbiddenException;
import ee.testiplatvorm.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.testiplatvorm.persistence.competencelevel.CompetenceLevelRepository;
import ee.testiplatvorm.persistence.question.Question;
import ee.testiplatvorm.persistence.question.QuestionMapper;
import ee.testiplatvorm.persistence.question.QuestionRepository;
import ee.testiplatvorm.persistence.questiontype.QuestionType;
import ee.testiplatvorm.persistence.questiontype.QuestionTypeRepository;
import ee.testiplatvorm.persistence.user.User;
import ee.testiplatvorm.persistence.user.UserMapper;
import ee.testiplatvorm.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static ee.testiplatvorm.Error.NO_PERMISSION_TO_CREATE_QUESTIONS;
import static ee.testiplatvorm.Status.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final CompetenceLevelRepository competenceLevelRepository;

    public List<QuestionResponseDto> findQuestionsBy(Integer competenceLevelId) {
        List<Question> questions = questionRepository.findQuestionsBy(competenceLevelId, STATUS_ACTIVE.getCode());
        List<QuestionResponseDto> questionResponseDtos = questionMapper.toQuestionResponseDtos(questions);
        return questionResponseDtos;
    }

    public void createQuestion(QuestionCreateRequestDto questionCreateRequestDto) {
        Integer userId = currentUserService.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> (new PrimaryKeyNotFoundException("userId", userId)));
        String userRole = user.getRole().getName();

        boolean isAllowedToCreateQuestion = userRole.equals("ADMIN") || userRole.equals("HALDUR");
        if (!isAllowedToCreateQuestion) {
            throw new ForbiddenException(NO_PERMISSION_TO_CREATE_QUESTIONS.getMessage(), NO_PERMISSION_TO_CREATE_QUESTIONS.name());
        }

        Integer questionTypeId = questionCreateRequestDto.getQuestionTypeId();
        QuestionType questionType = questionTypeRepository.findById(questionTypeId).orElseThrow(() -> new PrimaryKeyNotFoundException("questionTypeId", questionTypeId));


        competenceLevelRepository.findCompetenceLevelsBy()





        Question question = questionMapper.toQuestion(questionCreateRequestDto);





//   - kasutaja id ja tema rolli kontroll     - esimene asi ja tuleb läbi currentUserService,
//   kust saab teada id ja sellega koos käib ka roll läbi user tabeli
//
//
//  - küsimuse tüübi otsimine (+ 404)   -

//  - kompetentsi taseme otsimine (+ 404)


        //  - õigete vastuste arvu kontroll


//  - küsimuse mappimine ja ignoreeritud väljade täitmine

//  - küsimuse salvestamine


        //  - vastuste mappimine, question ja status külge panemine

        //  - vastuste salvestamine

        //  - tagastamine


    }

}
