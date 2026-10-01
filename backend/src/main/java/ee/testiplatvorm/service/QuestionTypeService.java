package ee.testiplatvorm.service;


import ee.testiplatvorm.controller.questiontype.dto.QuestionTypeDto;
import ee.testiplatvorm.persistence.questiontype.QuestionType;
import ee.testiplatvorm.persistence.questiontype.QuestionTypeMapper;
import ee.testiplatvorm.persistence.questiontype.QuestionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionTypeService {


    private final QuestionTypeRepository questionTypeRepository;
    private final QuestionTypeMapper questionTypeMapper;

    public List<QuestionTypeDto> findQuestionTypes() {
        List<QuestionType> questionTypes = questionTypeRepository.findAll();
        List<QuestionTypeDto> questionTypeDtos = questionTypeMapper.toQuestionTypeDtos(questionTypes);
        return questionTypeDtos;

    }

}
