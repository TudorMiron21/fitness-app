package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import tudor.work.model.UserHistoryWorkout;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserHistoryWorkoutRepository extends JpaRepository<UserHistoryWorkout, Long> {

    @Query("SELECT uhw " +
            "FROM UserHistoryWorkout uhw " +
            "WHERE uhw.user.email = :userEmail " +
            "AND uhw.isWorkoutDone = false")
    List<UserHistoryWorkout> findStartedWorkoutsByUserEmail(@Param("userEmail") String userEmail);

    @Query("SELECT uhw " +
            "FROM UserHistoryWorkout uhw " +
            "WHERE uhw.user.email = :emailUser " +
            "AND uhw.isWorkoutDone = false " +
            "AND uhw.workout.id= :workoutId")
    Optional<UserHistoryWorkout> findSpecificStartedWorkoutByUserEmail(@Param("workoutId") Long workoutId, @Param("emailUser") String emailUser);


    @Query(
            "SELECT uhe.currNoSeconds FROM UserHistoryWorkout uhw " +
            "JOIN uhw.user u " +
            "JOIN uhw.userHistoryModules uhm " + // assuming 'userHistoryModules' is the correct property name in UserHistoryWorkout entity
            "JOIN uhm.userHistoryExercises uhe " + // assuming 'userHistoryExercises' is the correct property name in UserHistoryModule entity
            "WHERE uhw.id = :workoutId AND u.email = :email"
    )
    List<Long> findAllUserHistoryExercisesSecondsByUserHistoryWorkoutId(@Param("workoutId") Long workoutId, @Param("email") String email);


    @Query(
            "SELECT uhe.caloriesBurned FROM UserHistoryWorkout uhw " +
                    "JOIN uhw.user u " +
                    "JOIN uhw.userHistoryModules uhm " + // assuming 'userHistoryModules' is the correct property name in UserHistoryWorkout entity
                    "JOIN uhm.userHistoryExercises uhe " + // assuming 'userHistoryExercises' is the correct property name in UserHistoryModule entity
                    "WHERE uhw.id = :workoutId AND u.email = :email"
    )
    List<Double> findAllUserHistoryExercisesCaloriesByUserHistoryWorkoutId(@Param("workoutId") Long id, @Param("email") String email);
}
