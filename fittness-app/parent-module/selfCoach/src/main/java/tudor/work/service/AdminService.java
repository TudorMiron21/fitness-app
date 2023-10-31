package tudor.work.service;

import com.sun.xml.bind.v2.TODO;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tudor.work.dto.ExerciseDto;
import tudor.work.dto.WorkoutDto;
import tudor.work.exceptions.AuthenticationExceptionHandler;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.model.Exercise;
import tudor.work.model.Workout;

import javax.transaction.Transactional;
import java.util.Optional;


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
                    .workouts(exercise.getWorkouts())
                    .build();

            exerciseService.saveExercise(newExercise);
        } else {
            throw new AuthenticationExceptionHandler("user not authenticated");
        }

    }

    @Transactional
    public void addWorkout(WorkoutDto workoutDto) throws DuplicatesException,NotFoundException, AuthenticationExceptionHandler {

        if (authorityService.isAdmin()) {

            try{
            Workout workout = Workout
                    .builder()
                    .name(workoutDto.getName())
                    .description(workoutDto.getDescription())
                    .exercises(workoutDto.getExercises())
                    .adder(authorityService.getUser())
                    .isDeleted(false)
                    .isGlobal(true)
                    .build();

                workoutService.saveWorkout(workout);
            }
            catch (DuplicatesException de)
            {
                throw new DuplicatesException("workout already in database");
            }
            catch (NotFoundException nfe)
            {
                throw new NotFoundException("User not found");
            }

        } else {
            throw new AuthenticationExceptionHandler("user not authenticated");
        }
    }

    public void addExerciseToWorkout(String exerciseName , String workoutName) throws AuthenticationExceptionHandler, NotFoundException, RuntimeException
    {

        if (authorityService.isAdmin()) {

            Optional<Workout> workout = workoutService.findWorkoutByName(workoutName);
            if(workout.isPresent())
            {
                log.info("is global:"+workout.get().isGlobal());
                Workout workoutActual = workout.get();

                //TODO: it always enters the else of this if.See why
                if(workoutActual.isGlobal())
                {

                    Optional<Exercise> exercise = exerciseService.getExerciseByName(exerciseName);
                    if(exercise.isPresent())
                    {
                        workoutActual.getExercises().add(exercise.get());
                        workoutService.saveWorkout(workoutActual);
                    }
                    else {
                        throw new NotFoundException("exercise not found in the database");
                    }
                }
                else
                {
                    throw new RuntimeException("admin cannot update local workouts");
                }

            }
            else {
                throw new NotFoundException("workout not found in the database");
            }

        } else {
            throw new AuthenticationExceptionHandler("user not authenticated");
        }
    }

}
