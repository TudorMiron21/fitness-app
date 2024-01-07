package tudor.work.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.UserHistoryExercise;

@Repository
public interface UserHistoryExerciseRepository extends JpaRepository<UserHistoryExercise,Long> {

}
