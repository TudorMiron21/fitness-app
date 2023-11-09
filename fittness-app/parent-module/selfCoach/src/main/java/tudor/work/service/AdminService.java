package tudor.work.service;

import com.sun.xml.bind.v2.TODO;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tudor.work.dto.ExerciseDto;
import tudor.work.dto.WorkoutDto;
import tudor.work.exceptions.AdminUpdateLocalWorkoutException;
import tudor.work.exceptions.AuthenticationExceptionHandler;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.model.Exercise;
import tudor.work.model.Workout;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AuthorityService authorityService;
    private final ExerciseService exerciseService;
    private final WorkoutService workoutService;

    @Transactional
    public void addExercise(ExerciseDto exercise) throws DuplicatesException, AuthenticationExceptionHandler {

        if (authorityService.isAdmin()) {
            //checks if the exercise already exists in the database
            if (exerciseService.getExerciseByName(exercise.getName()).isPresent()) {
                throw new DuplicatesException("exercise already in the database");
            }

            Exercise newExercise = Exercise
                    .builder()
                    .name(exercise.getName())
                    .description(exercise.getDescription())
                    .mediaUrl(exercise.getMediaUrl())
                    .isExerciseExclusive(exercise.isExerciseExclusive())
                    .category(exercise.getCategory())
                    .difficulty(exercise.getDifficulty())
//                    .workouts(exercise.getWorkouts())
                    .build();

            exerciseService.saveExercise(newExercise);
        } else {
            throw new AuthenticationExceptionHandler("user not authenticated");
        }

    }

    @Transactional
    public void addWorkout(WorkoutDto workoutDto) throws DuplicatesException, NotFoundException, AuthenticationExceptionHandler {

        if (authorityService.isAdmin()) {

            try {
                Workout workout = Workout
                        .builder()
                        .name(workoutDto.getName())
                        .description(workoutDto.getDescription())
                        .coverPhotoUrl(workoutDto.getCoverPhotoUrl())
                        .exercises(workoutDto.getExercises())
                        .adder(authorityService.getUser())
                        .isDeleted(false)
                        .isGlobal(true)
                        .build();

                workoutService.saveWorkout(workout);
            } catch (DuplicatesException de) {
                throw new DuplicatesException("workout already in database");
            } catch (NotFoundException nfe) {
                throw new NotFoundException("User not found");
            }

        } else {
            throw new AuthenticationExceptionHandler("user not authenticated");
        }
    }

    @Transactional
    public void addExerciseToWorkout(String exerciseName, String workoutName) throws AuthenticationExceptionHandler, NotFoundException, RuntimeException {

        if (authorityService.isAdmin()) {

            Optional<Workout> workout = workoutService.findWorkoutByName(workoutName);
            if (workout.isPresent()) {
                Workout workoutActual = workout.get();
                if (workoutActual.isGlobal()) {

                    Optional<Exercise> exercise = exerciseService.getExerciseByName(exerciseName);
                    if (exercise.isPresent()) {
                        Exercise exerciseActual = exercise.get();
                        workoutActual.getExercises().add(exerciseActual);
                    } else {
                        throw new NotFoundException("exercise not found in the database");
                    }
                } else {
                    throw new AdminUpdateLocalWorkoutException("admin cannot update local workouts");
                }
            } else {
                throw new NotFoundException("workout not found in the database");
            }

        } else {
            throw new AuthenticationExceptionHandler("user not authenticated");
        }
    }

    public void deleteWorkout(String workoutName) throws NotFoundException, AuthenticationExceptionHandler {
        if (authorityService.isAdmin()) {
            Workout workout = workoutService.findWorkoutByName(workoutName).orElseThrow(() -> new NotFoundException("workout not found in the database"));
            if (workout.isGlobal()) {
                workoutService.deleteWorkout(workout);
            } else {
                throw new AdminUpdateLocalWorkoutException("admin cannot delete local workouts");
            }
        } else {
            throw new AuthenticationExceptionHandler("user not authenticated");
        }

    }

    @Transactional
    public void deleteExerciseFromWorkout(String exerciseName, String workoutName)throws AuthenticationExceptionHandler , NotFoundException
    {
        //TODO : does not really work
        if (authorityService.isAdmin()) {
            Workout workout = workoutService.findWorkoutByName(workoutName).orElseThrow(() -> new NotFoundException("workout not found in the database"));
            if (workout.isGlobal()) {

                for (Exercise exercise: workout.getExercises()) {
                    if(exercise.getName().equals(exerciseName))
                    {
                        workout.removeExercise(exercise);
                    }
                    else {
                        throw new NotFoundException("exercise not present in workout exercise set");
                    }
                }
            } else {
                throw new AdminUpdateLocalWorkoutException("admin cannot delete local workouts");
            }
        } else {
            throw new AuthenticationExceptionHandler("user not authenticated");
        }
    }

}