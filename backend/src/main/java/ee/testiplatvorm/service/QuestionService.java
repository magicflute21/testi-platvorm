package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.question.dto.QuestionBankAnswerDto;
import ee.testiplatvorm.controller.question.dto.QuestionBankDto;
import ee.testiplatvorm.controller.question.dto.QuestionResponseDto;
import ee.testiplatvorm.persistence.question.Question;
import ee.testiplatvorm.persistence.question.QuestionMapper;
import ee.testiplatvorm.persistence.question.QuestionRepository;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswer;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswerMapper;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static ee.testiplatvorm.Status.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionAnswerMapper questionAnswerMapper;

    public List<QuestionResponseDto> findQuestionsBy(Integer competenceLevelId) {
        List<Question> questions = questionRepository.findQuestionsBy(competenceLevelId, STATUS_ACTIVE.getCode());
        List<QuestionResponseDto> questionResponseDtos = questionMapper.toQuestionResponseDtos(questions);
        return questionResponseDtos;
    }

    public List<QuestionBankDto> findAllQuestionsBy(Integer competenceId) {
        List<Question> allQuestions = questionRepository.findAllQuestionsBy(competenceId);
        List<QuestionBankDto> questionBankDtos = questionMapper.toQuestionBankDtos(allQuestions);

        for (QuestionBankDto questionBankDto : questionBankDtos) {
            List<QuestionAnswer> questionAnswers = questionAnswerRepository.findAnswersBy(questionBankDto.getQuestionId(), STATUS_ACTIVE.getCode());
            List<QuestionBankAnswerDto> questionBankAnswerDtos = questionAnswerMapper.toQuestionBankAnswerDtos(questionAnswers);
            questionBankDto.setAnswers(questionBankAnswerDtos);
        }

        return questionBankDtos;
    }
}
