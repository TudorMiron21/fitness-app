package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.ExerciseDto;
import tudor.work.exceptions.AuthenticationExceptionHandler;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.model.Exercise;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final AuthorityService authorityService;
    private final ExerciseService exerciseService;

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
                    .adder(exercise.getAdder())
                    .category(exercise.getCategory())
                    .difficulty(exercise.getDifficulty())
//                    .workouts(exercise.getWorkouts())
                    .build();

            exerciseService.saveExercise(newExercise);
        } else {
            throw new AuthenticationExceptionHandler("user not authenticated");
        }

    }
}
