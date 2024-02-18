package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.WorkoutResult;


@Repository
public interface WorkoutResultRepository extends JpaRepository<WorkoutResult,Long> {

}
