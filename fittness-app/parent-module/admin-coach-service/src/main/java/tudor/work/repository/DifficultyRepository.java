package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tudor.work.model.Difficulty;

import java.util.Optional;

@Repository

public interface DifficultyRepository extends JpaRepository<Difficulty,Repository> {
    Optional<Difficulty> findByDificultyLevel(String difficultyName);
}
