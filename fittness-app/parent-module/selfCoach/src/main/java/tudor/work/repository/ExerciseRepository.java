package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tudor.work.dto.SimplifiedExerciseDto;
import tudor.work.model.Exercise;
import tudor.work.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByIsExerciseExclusiveTrue();

    List<Exercise> findByIsExerciseExclusiveFalse();

    Optional<Exercise> findByName(String name);


    @Query("SELECT e " +
            "FROM Exercise e " +
            "WHERE e.isExerciseExclusive = false " +
            "AND LOWER(e.name) LIKE LOWER(CONCAT('%', :exerciseName, '%'))")
    Set<Exercise> findAllNonExclusiveExercisesByName(@Param("exerciseName") String exerciseName);

    @Query("SELECT e " +
            "FROM Exercise e " +
            "WHERE e.isExerciseExclusive = false ")
    Set<Exercise> findAllNonExclusiveExercises();

    List<Exercise> findAllByAdder(User coach);
}
