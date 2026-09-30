package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.question.dto.QuestionResponseDto;
import ee.testiplatvorm.persistence.question.Question;
import ee.testiplatvorm.persistence.question.QuestionMapper;
import ee.testiplatvorm.persistence.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static ee.testiplatvorm.Status.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    public List<QuestionResponseDto> findQuestionsBy(Integer competenceLevelId) {
        List<Question> questions = questionRepository.findQuestionsBy(competenceLevelId, STATUS_ACTIVE.getCode());
        List<QuestionResponseDto> questionResponseDtos = questionMapper.toQuestionResponseDtos(questions);
        return questionResponseDtos;
    }

    public List <AllQuestionsResponseDto> findAllQuestionsBy(Integer competenceId) {
        List <Question> allQuestions = questionRepository.findAllQuestionsBy(competenceId);
        List <AllQuestionsResponseDto> allQuestionsResponseDtos = questionMapper.toAllQuestionsResponseDtos(allQuestions);
        return allQuestionsResponseDtos;
    }
}
