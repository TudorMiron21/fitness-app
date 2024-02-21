package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import tudor.work.model.WorkoutResult;

import java.util.List;
import java.util.Set;


@Repository
public interface WorkoutResultRepository extends JpaRepository<WorkoutResult,Long> {


    @Query("SELECT wr FROM WorkoutResult wr " +
            "JOIN wr.userHistoryWorkout uhw " +
            "JOIN uhw.user u " +
            "WHERE u.email = :userEmail")
    List<WorkoutResult> findByUserEmail(@Param("userEmail") String userEmail);
}
