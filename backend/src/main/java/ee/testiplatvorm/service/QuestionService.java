package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.question.dto.QuestionBankAnswerDto;
import ee.testiplatvorm.controller.question.dto.QuestionBankDto;
import ee.testiplatvorm.controller.question.dto.QuestionResponseDto;
import ee.testiplatvorm.controller.question.dto.QuestionUpdateRequestDto;
import ee.testiplatvorm.infrastructure.exception.PrimaryKeyNotFoundException;
import ee.testiplatvorm.persistence.question.Question;
import ee.testiplatvorm.persistence.question.QuestionMapper;
import ee.testiplatvorm.persistence.question.QuestionRepository;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswer;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswerMapper;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static ee.testiplatvorm.Status.STATUS_ACTIVE;
import static ee.testiplatvorm.Status.STATUS_INACTIVE;

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
            handleGetQuestionsWithAnswers(questionBankDto);
        }
        return questionBankDtos;
    }

    public void updateQuestion(Integer questionId, QuestionUpdateRequestDto questionUpdateRequestDto) {
        Question question = getValidQuestionBy(questionId);
        questionMapper.updateQuestion(questionUpdateRequestDto, question);
        question.setUpdatedAt(OffsetDateTime.now());
        questionRepository.save(question);
    }

    public void deleteQuestion(Integer questionId) {
        Question question = getValidQuestionBy(questionId);
        question.setStatus(STATUS_INACTIVE.getCode());
        question.setUpdatedAt(OffsetDateTime.now());
        questionRepository.save(question);
    }

    public Question getValidQuestionBy(Integer questionId) {
        return questionRepository.findById(questionId).orElseThrow(() -> new PrimaryKeyNotFoundException("questionId", questionId));
    }

    private void handleGetQuestionsWithAnswers(QuestionBankDto questionBankDto) {
        List<QuestionAnswer> questionAnswers = questionAnswerRepository.findAnswersBy(questionBankDto.getQuestionId(), STATUS_ACTIVE.getCode());
        List<QuestionBankAnswerDto> questionBankAnswerDtos = questionAnswerMapper.toQuestionBankAnswerDtos(questionAnswers);
        questionBankDto.setAnswers(questionBankAnswerDtos);
    }
}
