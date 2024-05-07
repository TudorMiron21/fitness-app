package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tudor.work.model.Workout;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout,Long>, JpaSpecificationExecutor<Workout> {

    List<Workout> findAllByAdderId(Long userId);
}
