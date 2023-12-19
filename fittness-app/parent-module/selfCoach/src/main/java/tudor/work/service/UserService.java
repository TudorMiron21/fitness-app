package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import tudor.work.dto.*;
import tudor.work.exceptions.AuthorizationExceptionHandler;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.exceptions.UserAccessException;
import tudor.work.model.*;
import tudor.work.repository.ExerciseRepository;
import tudor.work.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.*;
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
    private final UserHistoryWorkoutService userHistoryWorkoutService;
    private final UserHistoryModuleService userHistoryModuleService;

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
                    .exerciseImageStartUrl(exercise.getExerciseImageStartUrl())
                    .exerciseImageEndUrl(exercise.getExerciseImageEndUrl())
                    .isExerciseExclusive(exercise.isExerciseExclusive())
                    .category(exercise.getCategory())
                    .difficulty(exercise.getDifficulty())
                    .muscleGroup(exercise.getMuscleGroup())
                    .equipment(exercise.getEquipment())
                    .build();
        }
    }

    public User getUserByEmail(String email) throws NotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("user " + email + " not found"));
    }

    public List<WorkoutDto> getAllWorkouts() {
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
                        {
                            try {
                                return WorkoutDto.
                                        builder().
                                        id(workout.getId()).
                                        name(workout.getName()).
                                        description(workout.getDescription()).
                                        coverPhotoUrl(workout.getCoverPhotoUrl()).
                                        difficultyLevel(workout.getDifficultyLevel()).
                                        isLikedByUser(workoutService.isWorkoutLikedByUser(workout, authorityService.getUser())).
                                        noLikes(workoutService.getNoLikes(workout)).
                                        exercises(workout.getExercises())
                                        .build();
                            } catch (NotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).toList();
    }

    public WorkoutDto getWorkoutByName(String name) throws NotFoundException, UserAccessException {
        Workout workout = workoutService.findWorkoutByName(name).orElseThrow(() -> new NotFoundException("Workout " + name + " not found"));

        if (workout.isGlobal() || authorityService.getUser().getId().equals(workout.getAdder())) {
            return WorkoutDto.
                    builder().
                    name(workout.getName()).
                    description(workout.getDescription()).
                    coverPhotoUrl(workout.getCoverPhotoUrl()).
                    difficultyLevel(workout.getDifficultyLevel()).
                    exercises(workout.getExercises())
                    .build();
        } else {
            throw new UserAccessException("user " + authorityService.getEmail() + " is not allowed to see other user's workouts");
        }
    }

    public void addWorkout(WorkoutDto workoutDto) throws NotFoundException, DuplicatesException {
        Workout workout = Workout
                .builder()
                .name(workoutDto.getName())
                .description((workoutDto.getDescription()))
                .coverPhotoUrl(workoutDto.getCoverPhotoUrl())
                .exercises(workoutDto.getExercises())
                .adder(authorityService.getUser())
                .isDeleted(false)
                .isGlobal(false)
                .build();
        workoutService.saveWorkout(workout);
    }

    @Transactional
    public void addExerciseToWorkout(String exerciseName, String workoutName) throws NotFoundException, AuthorizationExceptionHandler {
        //checks is the exercise is present in the database
        Exercise exercise = exerciseService.getExerciseByName(exerciseName).orElseThrow(() -> new NotFoundException("exercise " + exerciseName + " not found"));

        Workout workout = workoutService.findWorkoutByName(workoutName).orElseThrow(() -> new NotFoundException("workout " + workoutName + "not found"));

        if (!workout.isGlobal()) {
            workout.addExercise(exercise);
        } else {
            throw new AuthorizationExceptionHandler("user " + authorityService.getEmail() + " is not allowed to change global workouts");
        }


    }

    @Transactional
    public void likeWorkout(Long workoutId) throws NotFoundException {
        Workout workout = workoutService.findWorkoutById(workoutId).orElseThrow(() -> new NotFoundException("workout " + workoutId + " not found"));
        authorityService.getUser().likeWorkout(workout);

    }

    @Transactional

    public void unlikeWorkout(Long workoutId) throws NotFoundException {
        Workout workout = workoutService.findWorkoutById(workoutId).orElseThrow(() -> new NotFoundException("workout " + workoutId + " not found"));
        authorityService.getUser().unlikeWorkout(workout);

    }

    public List<WorkoutDto> getFirstSixMostLikedWorkouts() {

        return workoutService.getAllWorkouts()
                .stream()

                .map(
                        workout -> {
                            try {
                                return WorkoutDto.
                                        builder().
                                        id(workout.getId()).
                                        name(workout.getName()).
                                        description(workout.getDescription()).
                                        coverPhotoUrl(workout.getCoverPhotoUrl()).
                                        difficultyLevel(workout.getDifficultyLevel()).
                                        isLikedByUser(workoutService.isWorkoutLikedByUser(workout, authorityService.getUser())).
                                        noLikes(workoutService.getNoLikes(workout)).
                                        exercises(workout.getExercises())
                                        .build();
                            } catch (NotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .sorted(Comparator.comparing(WorkoutDto::getNoLikes))
                .limit(6)
                .toList();
    }

    public List<WorkoutDto> getTopWorkoutsForDifficultyLevel(Double lowerLimit, Double upperLimit) {

        return workoutService.getAllWorkouts()
                .stream()
                .filter(workout -> workout.getDifficultyLevel() >= lowerLimit && workout.getDifficultyLevel() < upperLimit)
                .map(
                        workout -> {
                            try {
                                return WorkoutDto.
                                        builder().
                                        id(workout.getId()).
                                        name(workout.getName()).
                                        description(workout.getDescription()).
                                        coverPhotoUrl(workout.getCoverPhotoUrl()).
                                        difficultyLevel(workout.getDifficultyLevel()).
                                        isLikedByUser(workoutService.isWorkoutLikedByUser(workout, authorityService.getUser())).
                                        noLikes(workoutService.getNoLikes(workout)).
                                        exercises(workout.getExercises())
                                        .build();
                            } catch (NotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .sorted(Comparator.comparing(WorkoutDto::getNoLikes))
                .limit(6)
                .toList();
    }

    @Transactional
    public Long startWorkout(Long workoutId) throws NotFoundException {

        Workout workout = workoutService
                .findWorkoutById(workoutId)
                .orElseThrow(() -> new NotFoundException("Workout " + workoutId + " not found"));

        UserHistoryWorkout userHistoryWorkout = UserHistoryWorkout
                .builder()
                .workout(workout)
                .userHistoryModules(new ArrayList<>())
                .user(authorityService.getUser())
                .isWorkoutDone(false)
                .build();

        UserHistoryWorkout userHistoryWorkoutRetrieve = userHistoryWorkoutService.save(userHistoryWorkout);

        return userHistoryWorkoutRetrieve.getId();
    }


    @Transactional
    public Long saveModule(RequestSaveModuleDto requestSaveModuleDto) throws NotFoundException {
        UserHistoryWorkout userHistoryWorkout =
                userHistoryWorkoutService
                        .getUserHistoryWorkout(requestSaveModuleDto
                                .getParentUserHistoryWorkoutId())
                        .orElseThrow(() -> new NotFoundException("UserHistoryWorkout entry with id " + requestSaveModuleDto.getParentUserHistoryWorkoutId() + " not found"));


        UserHistoryModule userHistoryModule = UserHistoryModule
                .builder()
                .userHistoryWorkout(userHistoryWorkout)
                .userHistoryExercises(new ArrayList<>())
                .noSets(requestSaveModuleDto.getNoSets())
                .build();

        if (userHistoryWorkout.getUserHistoryModules() == null) {
            userHistoryWorkout.setUserHistoryModules(new ArrayList<>());
        }

        userHistoryWorkout.addUserHistoryModule(userHistoryModule);

        // Save userHistoryWorkout to the database
        userHistoryWorkoutService.saveAndFlush(userHistoryWorkout);


        UserHistoryWorkout userHistoryWorkoutRetrieve = userHistoryWorkoutService.getUserHistoryWorkout(userHistoryWorkout.getId()).orElseThrow();

        return userHistoryWorkoutRetrieve.getUserHistoryModules().get(userHistoryWorkoutRetrieve.getUserHistoryModules().size() - 1).getId();
    }


    @Transactional
    public void addExerciseToModule(Long userHistoryModuleId, RequestUserHistoryExercise requestUserHistoryExercise) throws NotFoundException {

        UserHistoryModule userHistoryModule = userHistoryModuleService.getModuleById(userHistoryModuleId);

        UserHistoryExercise userHistoryExercise = UserHistoryExercise
                .builder()
                .exercise(requestUserHistoryExercise.getExercise())
                .userHistoryModule(requestUserHistoryExercise.getUserHistoryModule())
                .currNoSeconds(requestUserHistoryExercise.getCurrNoSeconds())
                .isDone(requestUserHistoryExercise.isDone())
                .build();

        if (requestUserHistoryExercise.getExercise().isHasNoReps()) {
            userHistoryExercise.setNoReps(requestUserHistoryExercise.getNoReps());
        }

        if (requestUserHistoryExercise.getExercise().isHasWeight()) {
            userHistoryExercise.setWeight(requestUserHistoryExercise.getWeight());
        }

        userHistoryModule.addExerciseToUserHistoryExercises(
                userHistoryExercise
        );

    }


    private Double calculateDifficultyLevel(Set<Exercise> exercises) {

        return exercises.stream().mapToDouble(exercise -> exercise.getDifficulty().getDifficultyLevelNumber()).average().orElse(0.0);
    }

    public void addWorkout(PostWorkoutRequestDto postWorkoutRequestDto) throws NotFoundException {

        Workout workout = Workout
                .builder()
                .name(postWorkoutRequestDto.getName())
                .description(postWorkoutRequestDto.getDescription())
                .exercises(postWorkoutRequestDto.getExercises())
                .adder(authorityService.getUser())
                .isGlobal(false)
                .isDeleted(false)
                .difficultyLevel(this.calculateDifficultyLevel(postWorkoutRequestDto.getExercises()))
                .build();
        workoutService.saveWorkout(workout);
    }


    @Transactional
    public void finishWorkout(Long userHistoryWotkoutId) throws NotFoundException {

        UserHistoryWorkout userHistoryWorkout = userHistoryWorkoutService
                .getUserHistoryWorkout(userHistoryWotkoutId)
                .orElseThrow(() -> new NotFoundException("user histtory workout" + userHistoryWotkoutId + " not found"));

        userHistoryWorkout.setIsWorkoutDone(true);

    }


//    public List<Workout> getStartedWorkouts(Long idUser) {
//
//
//    }
}
