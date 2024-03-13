package tudor.work.repository;


import org.springframework.stereotype.Repository;
import tudor.work.model.Exercise;

@Repository
public interface ExerciseRepository {
    Exercise save(Exercise exercise);
}
