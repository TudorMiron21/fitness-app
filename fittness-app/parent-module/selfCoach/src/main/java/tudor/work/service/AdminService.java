package tudor.work.service;

import com.sun.xml.bind.v2.TODO;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tudor.work.dto.ExerciseDto;
import tudor.work.dto.WorkoutDto;
import tudor.work.exceptions.AdminUpdateLocalWorkoutException;
import tudor.work.exceptions.AuthorizationExceptionHandler;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.model.Exercise;
import tudor.work.model.Workout;

import javax.transaction.Transactional;
import java.util.Iterator;
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
    public void addExercise(ExerciseDto exercise) throws DuplicatesException, AuthorizationExceptionHandler {

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
                    .coverPhotoUrl(exercise.getCoverPhotoUrl())
                    .isExerciseExclusive(exercise.isExerciseExclusive())
                    .category(exercise.getCategory())
                    .difficulty(exercise.getDifficulty())
//                    .workouts(exercise.getWorkouts())
                    .build();

            exerciseService.saveExercise(newExercise);
        } else {
            throw new AuthorizationExceptionHandler("user not authorised");
        }

    }

    @Transactional
    public void addWorkout(WorkoutDto workoutDto) throws DuplicatesException, NotFoundException, AuthorizationExceptionHandler {

        if (authorityService.isAdmin()) {

            try {
                Workout workout = Workout
                        .builder()
                        .name(workoutDto.getName())
                        .description(workoutDto.getDescription())
                        .coverPhotoUrl(workoutDto.getCoverPhotoUrl())
                        .difficultyLevel(this.calculateDifficultyLevel(workoutDto.getExercises()))
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
            throw new AuthorizationExceptionHandler("user not authorised");
        }
    }

    private Double calculateDifficultyLevel(Set<Exercise> exercises) {

        return exercises.stream().mapToDouble(exercise -> exercise.getDifficulty().getDifficultyLevelNumber()).average().orElse(0.0);
    }

    @Transactional
    public void addExerciseToWorkout(String exerciseName, String workoutName) throws AuthorizationExceptionHandler, NotFoundException, RuntimeException {

        if (authorityService.isAdmin()) {

            Optional<Workout> workout = workoutService.findWorkoutByName(workoutName);
            if (workout.isPresent()) {
                Workout workoutActual = workout.get();
                if (workoutActual.isGlobal()) {

                    Optional<Exercise> exercise = exerciseService.getExerciseByName(exerciseName);
                    if (exercise.isPresent()) {
                        Exercise exerciseActual = exercise.get();
                        workoutActual.getExercises().add(exerciseActual);
                        Double currDifficultyLevel = workoutActual.getDifficultyLevel();
                        workoutActual.setDifficultyLevel((currDifficultyLevel + exerciseActual.getDifficulty().getDifficultyLevelNumber()) / (long) workoutActual.getExercises().size());
                    } else {
                        throw new NotFoundException("exercise " + exerciseName + " not found in the database");
                    }
                } else {
                    throw new AdminUpdateLocalWorkoutException("admin cannot update local workouts");
                }
            } else {
                throw new NotFoundException("workout " + workoutName + " not found in the database");
            }

        } else {
            throw new AuthorizationExceptionHandler("user not authorised");
        }
    }

    public void deleteWorkout(String workoutName) throws NotFoundException, AuthorizationExceptionHandler {
        if (authorityService.isAdmin()) {
            Workout workout = workoutService.findWorkoutByName(workoutName).orElseThrow(() -> new NotFoundException("workout not found in the database"));
            if (workout.isGlobal()) {
                workoutService.deleteWorkout(workout);
            } else {
                throw new AdminUpdateLocalWorkoutException("admin cannot delete local workouts");
            }
        } else {
            throw new AuthorizationExceptionHandler("user not authorised");
        }

    }

    @Transactional
    public void deleteExerciseFromWorkout(String exerciseName, String workoutName) throws AuthorizationExceptionHandler, NotFoundException {
        if (authorityService.isAdmin()) {
            Workout workout = workoutService.findWorkoutByName(workoutName).orElseThrow(() -> new NotFoundException("workout not found in the database"));
            if (workout.isGlobal()) {

                Exercise foundExercise = null;
                Iterator<Exercise> iterator = workout.getExercises().iterator();
                while (iterator.hasNext()) {
                    Exercise exercise = iterator.next();
                    if (exercise.getName().equals(exerciseName)) {
                        iterator.remove(); // Use iterator to remove the element
                        foundExercise = exercise;
                        break; // Exit the loop once the exercise is found and removed
                    }
                }

                if (foundExercise != null) {
                    workout.removeExercise(foundExercise);
                    workout.setDifficultyLevel(this.calculateDifficultyLevel(workout.getExercises()));
                }
                else
                    throw new NotFoundException("exercise not present in workout exercise set");
            } else {
                throw new AdminUpdateLocalWorkoutException("admin cannot delete local workouts");
            }
        } else {
            throw new AuthorizationExceptionHandler("user not authorised");
        }
    }

}