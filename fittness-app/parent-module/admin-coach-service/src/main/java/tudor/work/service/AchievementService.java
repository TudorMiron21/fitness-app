package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.Achievement;
import tudor.work.repository.AchievementRepository;

@Service
@RequiredArgsConstructor
public class AchievementService {
    private final AchievementRepository achievementRepository;

    public Achievement save(Achievement achievement) {
        return achievementRepository.save(achievement);
    }
}
