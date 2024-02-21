package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tudor.work.model.*;

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
                    "JOIN uhw.userHistoryModules uhm " +
                    "JOIN uhm.userHistoryExercises uhe " +
                    "WHERE uhw.id = :workoutId "
    )
    List<Long> findAllUserHistoryExercisesSecondsByUserHistoryWorkoutId(@Param("workoutId") Long workoutId);


    @Query(
            "SELECT uhe.caloriesBurned FROM UserHistoryWorkout uhw " +
                    "JOIN uhw.user u " +
                    "JOIN uhw.userHistoryModules uhm " +
                    "JOIN uhm.userHistoryExercises uhe " +
                    "WHERE uhw.id = :workoutId "
    )
    List<Double> findAllUserHistoryExercisesCaloriesByUserHistoryWorkoutId(@Param("workoutId") Long id);

    @Query(
            "SELECT uhe.weight * uhe.noReps FROM UserHistoryWorkout uhw " +
                    "JOIN uhw.user u " +
                    "JOIN uhw.userHistoryModules uhm " +
                    "JOIN uhm.userHistoryExercises uhe " +
                    "WHERE uhw.id = :workoutId "
    )
    List<Double> findAllUserHistoryVolumesWeightsByUserHistoryWorkoutId(@Param("workoutId")Long userHistoryWorkoutId);


    @Query(
            "SELECT c FROM UserHistoryWorkout uhw " +
                    "JOIN uhw.user u " +
                    "JOIN uhw.userHistoryModules uhm " +
                    "JOIN uhm.userHistoryExercises uhe " +
                    "JOIN uhe.exercise e " +
                    "JOIN e.category c " +
                    "WHERE uhw.id = :workoutId "
    )
    List<Category> findAllCategoriesByUserHistoryWorkoutId(@Param("workoutId")Long id);

    @Query(
            "SELECT d FROM UserHistoryWorkout uhw " +
                    "JOIN uhw.user u " +
                    "JOIN uhw.userHistoryModules uhm " +
                    "JOIN uhm.userHistoryExercises uhe " +
                    "JOIN uhe.exercise e " +
                    "JOIN e.difficulty d " +
                    "WHERE uhw.id = :workoutId "
    )
    List<Difficulty> findAllDifficultiesByUserHistoryWorkoutId(@Param("workoutId")Long id);


    @Query(
            "SELECT mg FROM UserHistoryWorkout uhw " +
                    "JOIN uhw.user u " +
                    "JOIN uhw.userHistoryModules uhm " +
                    "JOIN uhm.userHistoryExercises uhe " +
                    "JOIN uhe.exercise e " +
                    "JOIN e.muscleGroup mg " +
                    "WHERE uhw.id = :workoutId "
    )
    List<MuscleGroup> findAllMuscleGroupsByUserHistoryWorkoutId(@Param("workoutId")Long id);


    @Query(
            "SELECT uhe FROM UserHistoryWorkout uhw " +
                    "JOIN uhw.user u " +
                    "JOIN uhw.userHistoryModules uhm " +
                    "JOIN uhm.userHistoryExercises uhe " +
                    "WHERE uhw.id = :workoutId "
    )
    List<UserHistoryExercise> findAllUserHistoryExercisesByUserHistoryWorkoutId(@Param("workoutId")Long id);
}
