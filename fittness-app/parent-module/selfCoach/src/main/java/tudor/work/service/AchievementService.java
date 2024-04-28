package tudor.work.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.Achievement;
import tudor.work.repository.AchievementRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;

    public List<Achievement> findAll(){
        return achievementRepository.findAll();
    }
}
