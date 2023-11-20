package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.UserHistoryWorkout;


@Repository
public interface UserHistoryWorkoutRepository extends JpaRepository<UserHistoryWorkout,Long> {
}
