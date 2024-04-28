package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.Achievement;
import tudor.work.service.AchievementService;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement,Long> {
}
