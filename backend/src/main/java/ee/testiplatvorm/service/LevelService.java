package ee.testiplatvorm.service;

import ee.testiplatvorm.controller.level.dto.LevelDto;
import ee.testiplatvorm.persistence.level.Level;
import ee.testiplatvorm.persistence.level.LevelMapper;
import ee.testiplatvorm.persistence.level.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;
    private final LevelMapper levelMapper;

    public List<LevelDto> findAllLevels() {
        List<Level> levels = levelRepository.findAll(Sort.by("level", "name"));
        return levelMapper.toLevelDtos(levels);
    }
}
