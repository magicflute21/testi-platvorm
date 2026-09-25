package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.question.dto.QuestionResponseDto;
import ee.testiplatvorm.persistence.question.Question;
import ee.testiplatvorm.persistence.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static ee.testiplatvorm.Status.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final

    public List<QuestionResponseDto> findQuestionsById(Integer questionId){
        List<Question> questions = questionRepository.findQuestionsBy(questionId, STATUS_ACTIVE.getCode());
        List<QuestionResponseDto> questionResponseDtos = questionMapper.toQuestionResponseDtos(questions);
        return questionResponseDtos;

    }
}
