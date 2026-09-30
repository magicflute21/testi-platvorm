package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.competencelevel.dto.CompetenceLevelResponseDto;
import ee.testiplatvorm.persistence.competencelevel.CompetenceLevel;
import ee.testiplatvorm.persistence.competencelevel.CompetenceLevelMapper;
import ee.testiplatvorm.persistence.competencelevel.CompetenceLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static ee.testiplatvorm.Status.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor
public class CompetenceLevelService {

    private final CompetenceLevelRepository competenceLevelRepository;
    private final CompetenceLevelMapper competenceLevelMapper;

    public List<CompetenceLevelResponseDto> findCompetenceLevels(Integer competenceId) {
        List<CompetenceLevel> competenceLevels = competenceLevelRepository.findCompetenceLevelsBy(competenceId, STATUS_ACTIVE.getCode());
        List<CompetenceLevelResponseDto> competenceLevelResponseDtos = competenceLevelMapper.toCompetenceLevelResponseDtos(competenceLevels);
        return competenceLevelResponseDtos;
    }
}
