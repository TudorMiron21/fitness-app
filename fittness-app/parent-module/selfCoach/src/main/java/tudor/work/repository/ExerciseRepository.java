package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.Exercise;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByIsExerciseExclusiveTrue();
    List<Exercise> findByIsExerciseExclusiveFalse();

    Optional<Exercise> findByName(String name);

}
