package tudor.work.repository;

import org.springframework.stereotype.Repository;

import tudor.work.model.Difficulty;

import java.util.Optional;

@Repository

public interface DifficultyRepository {
    Optional<Difficulty> findByDifficultyLevel(String difficultyName);
}
