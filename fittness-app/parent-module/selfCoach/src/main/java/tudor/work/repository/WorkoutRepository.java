package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.Workout;

import java.util.Optional;


@Repository
public interface WorkoutRepository extends JpaRepository<Workout,Long> {

    Optional<Workout> findByName(String name);

}
