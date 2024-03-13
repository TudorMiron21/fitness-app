package tudor.work.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.Exercise;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise,Long> {
    Exercise save(Exercise exercise);
}
