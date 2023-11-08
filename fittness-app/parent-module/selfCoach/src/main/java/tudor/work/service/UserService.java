package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import tudor.work.dto.ExerciseDto;
import tudor.work.dto.WorkoutDto;
import tudor.work.exceptions.AuthenticationExceptionHandler;
import tudor.work.exceptions.UserAccessException;
import tudor.work.model.Exercise;
import tudor.work.model.User;
import tudor.work.model.Workout;
import tudor.work.repository.ExerciseRepository;
import tudor.work.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ExerciseService exerciseService;
    private final ExerciseRepository exerciseRepository;
    private final AuthorityService authorityService;
    private final WorkoutService workoutService;

    public List<ExerciseDto> getAllExercises() {

        List<? extends GrantedAuthority> authorities = authorityService.getUserAuthorities().stream().toList();

        List<ExerciseDto> exercises = exerciseService.getAllUserExercises();
        if (authorityService.isPayingUser() || authorityService.isCoach() || authorityService.isAdmin()) {
            exercises.addAll(exerciseService.getAllExclusiveExercises());
        }

        return exercises;
    }


    public ExerciseDto getExerciseByName(String name) throws NotFoundException, RuntimeException {
        Exercise exercise = exerciseRepository.findByName(name).stream().findFirst().orElseThrow(() -> (new NotFoundException("exercise not found")));

        if (exercise.isExerciseExclusive() && !authorityService.isUserExclusive()) {

            throw new RuntimeException("user does not have access!");
        } else {
            return ExerciseDto
                    .builder()
                    .name(exercise.getName())
                    .description(exercise.getDescription())
                    .mediaUrl(exercise.getMediaUrl())
                    .isExerciseExclusive(exercise.isExerciseExclusive())
                    .category(exercise.getCategory())
                    .difficulty(exercise.getDifficulty())
                    .build();
        }
    }

    public User getUserByName(String name) throws NotFoundException {
        return userRepository.findByUsername(name).orElseThrow(() -> new NotFoundException("user " + name + " not found"));
    }

    public List<WorkoutDto> getAllWorkouts()  {
        List<Workout> adminWorkouts = workoutService.getAllWorkouts()
                .stream()
                .filter(Workout::isGlobal)
                .toList();

        List<Workout> userWorkouts = workoutService.getAllWorkouts()
                .stream()
                .filter(workout -> {
                    try {
                        return authorityService.getUser().equals(workout.getAdder());
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        return Stream.concat(adminWorkouts.stream(), userWorkouts.stream())
                .toList().stream().map(workout ->
                    WorkoutDto.
                            builder().
                            name(workout.getName()).
                            description(workout.getDescription()).
                            coverPhotoUrl(workout.getCoverPhotoUrl()).
                            exercises(workout.getExercises())
                            .build()
                ).toList();
    }

    public WorkoutDto getWorkoutByName(String name) throws NotFoundException, UserAccessException
    {
        Workout workout =  workoutService.findWorkoutByName(name).orElseThrow(()-> new NotFoundException("Workout " + name+  " not found"));

        if(workout.isGlobal() || authorityService.getUser().getId().equals(workout.getAdder()))
        {
            return WorkoutDto.
                    builder().
                    name(workout.getName()).
                    description(workout.getDescription()).
                    coverPhotoUrl(workout.getCoverPhotoUrl()).
                    exercises(workout.getExercises())
                    .build();
        }
        else {
            throw new UserAccessException("user " + authorityService.getUserName() +" is not allowed to see other user's workouts");
        }

    }

}
