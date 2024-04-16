package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import tudor.work.dto.*;
import tudor.work.exceptions.AuthorizationExceptionHandler;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.exceptions.LastWorkoutResultEmpty;
import tudor.work.exceptions.UserAccessException;
import tudor.work.model.*;
import tudor.work.repository.ExerciseRepository;
import tudor.work.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.transaction.annotation.Propagation.*;

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
    private final UserHistoryExerciseService userHistoryExerciseService;
    private final WorkoutResultService workoutResultService;
    private final StatisticsService statisticsService;
    private final PersonalRecordService personalRecordService;


    public User findById(Long id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("user with id:" + id + " not found"));
    }

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

    //TODO: change the parameters from name(String) to Id(Long)
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
    public void updateExerciseToWorkout(
            Long userHistoryExerciseId,
            DetailsUserHistoryExerciseDto requestUpdateUserHistoryExerciseDto
    ) throws NotFoundException {

        UserHistoryExercise userHistoryExercise = userHistoryExerciseService.findById(userHistoryExerciseId);

        userHistoryExercise.setCurrNoSeconds(requestUpdateUserHistoryExerciseDto.getCurrentNoSeconds());
        userHistoryExercise.setNoReps(requestUpdateUserHistoryExerciseDto.getNoReps());
        userHistoryExercise.setWeight(requestUpdateUserHistoryExerciseDto.getWeight());

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
                .startedWorkoutDateAndTime(LocalDateTime.now())
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


        if (requestUserHistoryExercise.getExercise().isHasNoReps() || requestUserHistoryExercise.getExercise().isHasWeight()) {

            if (requestUserHistoryExercise.getExercise().isHasNoReps()) {
                userHistoryExercise.setNoReps(requestUserHistoryExercise.getNoReps());

                Optional<PersonalRecord> personalRecord = personalRecordService.findByUserIdAndExerciseId(authorityService.getUserId(), requestUserHistoryExercise.getExercise().getId());

                //if the exercise is already present in the personal record table for this user
                if (personalRecord.isPresent()) {
                    PersonalRecord personalRecordValue = personalRecord.get();

                    if (personalRecordValue.getMaxNoReps() < requestUserHistoryExercise.getNoReps()) {
                        personalRecordValue.setMaxNoReps(requestUserHistoryExercise.getNoReps());
                        personalRecordValue.setMaxTime(requestUserHistoryExercise.getCurrNoSeconds());
                    }
                } else {

                    personalRecordService.save(
                            PersonalRecord
                                    .builder()
                                    .exercise(requestUserHistoryExercise.getExercise())
                                    .user(authorityService.getUser())
                                    .maxNoReps(requestUserHistoryExercise.getNoReps())
                                    .maxTime(requestUserHistoryExercise.getCurrNoSeconds())
                                    .build()
                    );
                }

            }

            if (requestUserHistoryExercise.getExercise().isHasWeight()) {
                userHistoryExercise.setWeight(requestUserHistoryExercise.getWeight());

                Optional<PersonalRecord> personalRecord = personalRecordService.findByUserIdAndExerciseId(authorityService.getUserId(), requestUserHistoryExercise.getExercise().getId());

                if (personalRecord.isPresent()) {
                    PersonalRecord personalRecordValue = personalRecord.get();

                    if (personalRecordValue.getMaxWeight() < requestUserHistoryExercise.getWeight()) {
                        personalRecordValue.setMaxWeight(requestUserHistoryExercise.getWeight());
                        personalRecordValue.setMaxTime(requestUserHistoryExercise.getCurrNoSeconds());

                    }
                } else {

                    personalRecordService.save(
                            PersonalRecord
                                    .builder()
                                    .exercise(requestUserHistoryExercise.getExercise())
                                    .user(authorityService.getUser())
                                    .maxWeight(requestUserHistoryExercise.getWeight())
                                    .maxTime(requestUserHistoryExercise.getCurrNoSeconds())
                                    .build()
                    );
                }
            }
        } else {
            userHistoryExercise.setCaloriesBurned(requestUserHistoryExercise.getCaloriesBurned());


            Optional<PersonalRecord> personalRecord = personalRecordService.findByUserIdAndExerciseId(authorityService.getUserId(), requestUserHistoryExercise.getExercise().getId());

            if (personalRecord.isPresent()) {
                PersonalRecord personalRecordValue = personalRecord.get();

                if (personalRecordValue.getMaxCalories() < requestUserHistoryExercise.getCaloriesBurned()) {
                    personalRecordValue.setMaxCalories(requestUserHistoryExercise.getCaloriesBurned());
                    personalRecordValue.setMaxTime(requestUserHistoryExercise.getCurrNoSeconds());
                }
            } else {

                personalRecordService.save(
                        PersonalRecord
                                .builder()
                                .exercise(requestUserHistoryExercise.getExercise())
                                .user(authorityService.getUser())
                                .maxCalories(requestUserHistoryExercise.getCaloriesBurned())
                                .maxTime(requestUserHistoryExercise.getCurrNoSeconds())
                                .build()
                );
            }

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
    public WorkoutResult saveUserHistoryWorkoutAndGetWorkoutResult(Long userHistoryWorkoutId) throws NotFoundException {

        UserHistoryWorkout userHistoryWorkout = userHistoryWorkoutService
                .getUserHistoryWorkout(userHistoryWorkoutId)
                .orElseThrow(() -> new NotFoundException("user history workout" + userHistoryWorkoutId + " not found"));

        userHistoryWorkout.setIsWorkoutDone(true);
        userHistoryWorkout.setFinishedWorkoutDateAndTime(LocalDateTime.now());

        //saves the workout result first
        WorkoutResult workoutResult = workoutResultService
                .saveAndFlush(WorkoutResult
                        .builder()
                        .userHistoryWorkout(userHistoryWorkoutService.findById(userHistoryWorkoutId))
                        .build());

        return workoutResult;
    }


    public void finishWorkout(Long userHistoryWorkoutId) throws NotFoundException, ExecutionException, InterruptedException {

        WorkoutResult workoutResult = saveUserHistoryWorkoutAndGetWorkoutResult(userHistoryWorkoutId);


        User user = authorityService.getUser();
        //TODO: async generate the stats for the newly added workout history entry
        CompletableFuture<WorkoutResult> result = statisticsService.getStatistics(userHistoryWorkoutId, workoutResult.getId(), user);

        WorkoutResult completedWorkoutResult = result.get();

        workoutResult.setTotalTime(completedWorkoutResult.getTotalTime());
        workoutResult.setTotalCaloriesBurned(completedWorkoutResult.getTotalCaloriesBurned());
        workoutResult.setTotalVolume(completedWorkoutResult.getTotalVolume());
        workoutResult.setResultCategoryPercentages(completedWorkoutResult.getResultCategoryPercentages());
        workoutResult.setResultDifficultyPercentages(completedWorkoutResult.getResultDifficultyPercentages());
        workoutResult.setResultMuscleGroupPercentages(completedWorkoutResult.getResultMuscleGroupPercentages());


        workoutResultService.save(workoutResult);
    }



    public Boolean userExists(String emailUser) {
        return userRepository.findByEmail(emailUser).isPresent();
    }

    public List<WorkoutDto> getStartedWorkouts(String emailUser) throws NotFoundException {

        List<UserHistoryWorkout> startedUserHistoryWorkouts;
        if (userExists(emailUser)) {
            startedUserHistoryWorkouts = userHistoryWorkoutService.getStartedUserHistoryWorkoutsByUserEmail(emailUser);
            List<Workout> startedWorkoutsList = startedUserHistoryWorkouts
                    .stream()
                    .map(userHistoryWorkout -> workoutService
                            .findWorkoutById(userHistoryWorkout
                                    .getWorkout()
                                    .getId())
                            .get())
                    .toList();

            return startedWorkoutsList.stream().map(workout ->

                    WorkoutDto.
                            builder().
                            id(workout.getId()).
                            name(workout.getName()).
                            description(workout.getDescription()).
                            coverPhotoUrl(workout.getCoverPhotoUrl()).
                            difficultyLevel(workout.getDifficultyLevel()).
                            exercises(workout.getExercises())
                            .build()
            ).toList();
        } else {
            throw new NotFoundException("user with email " + emailUser + " not found");
        }

    }

    @Transactional
    public ResponseWorkoutPresentInUserHistoryDto isWorkoutPresentInUserHistory(Long workoutId, String emailUser) throws NotFoundException {

        UserHistoryWorkout userHistoryWorkout = userHistoryWorkoutService.isWorkoutPresentInUserHistory(workoutId, emailUser);

        Long userHistoryWorkoutId = userHistoryWorkout.getId();

        Integer moduleIndex = userHistoryWorkout.getUserHistoryModules().size();

        Integer exerciseIndex = userHistoryWorkout.getUserHistoryModules().get(moduleIndex - 1).getUserHistoryExercises().size();

        Integer noSetsLastModule = userHistoryWorkout.getUserHistoryModules().get(moduleIndex - 1).getNoSets();

        Long userHistoryModuleId = userHistoryWorkout.getUserHistoryModules().get(moduleIndex - 1).getId();

        Long userHistoryExerciseId = userHistoryWorkout.getUserHistoryModules().get(moduleIndex - 1).getUserHistoryExercises().get(exerciseIndex - 1).getId();

        return ResponseWorkoutPresentInUserHistoryDto
                .builder()
                .userHistoryWorkoutId(userHistoryWorkoutId)
                .userHistoryModuleId(userHistoryModuleId)
                .userHistoryExerciseId(userHistoryExerciseId)
                .exerciseIndex(exerciseIndex - 1)
                .moduleIndex(moduleIndex - 1)
                .noSetsLastModule(noSetsLastModule)
                .build();
    }

    public ResponseLastEntryUserHistoryExerciseDto getLastEntryUserExerciseHistory(Long workoutId, String userEmail) throws NotFoundException {

        //if the exception is triggered it means that there is no started workout in the history for the specified email
        UserHistoryWorkout userHistoryWorkout = userHistoryWorkoutService.isWorkoutPresentInUserHistory(workoutId, userEmail);

        Boolean isFirstValue = false;
        Boolean isDone = false;
        Long userHistoryExerciseId = 0L;
        Integer noModules = userHistoryWorkout.getUserHistoryModules().size();
        UserHistoryModule lastUserHistoryModule;
        Long userHistoryModuleId = 0L;

        if (!userHistoryWorkout.getUserHistoryModules().isEmpty()) {

            lastUserHistoryModule = userHistoryWorkout.getUserHistoryModules().get(noModules - 1);
            Integer noDoneExercises = lastUserHistoryModule.getUserHistoryExercises().stream().filter(userHistoryExercise -> userHistoryExercise.isDone()).toList().size();
            Integer noExercises = lastUserHistoryModule.getUserHistoryExercises().size();
            UserHistoryExercise lastUserHistoryExercise = lastUserHistoryModule.getUserHistoryExercises().get(noExercises - 1);

            userHistoryExerciseId = lastUserHistoryExercise.getId();
            userHistoryModuleId = lastUserHistoryModule.getId();

            if (noDoneExercises.equals(lastUserHistoryModule.getNoSets()))
                isFirstValue = true;
            if (lastUserHistoryExercise.isDone())
                isDone = true;
        } else {
            isDone = true;
            isFirstValue = true;
        }
        return ResponseLastEntryUserHistoryExerciseDto
                .builder()
                .isExerciseDone(isDone)
                .isFirstExercise(isFirstValue)
                .userHistoryExerciseId(userHistoryExerciseId)
                .userHistoryModuleId(userHistoryModuleId)
                .build();
    }

    @Transactional
    public void updateUserHistoryModule(Long userHistoryModuleId, DetailsUserHistoryModuleDto requestUpdateUserHistoryModuleDto) throws NotFoundException {

        UserHistoryModule userHistoryModule = userHistoryModuleService.getModuleById(userHistoryModuleId);

        userHistoryModule.setNoSets(requestUpdateUserHistoryModuleDto.getNoSets());

    }

    public DetailsUserHistoryModuleDto getUserHistoryModuleDetails(Long userHistoryModuleId) throws NotFoundException {

        UserHistoryModule userHistoryModule = userHistoryModuleService.getModuleById(userHistoryModuleId);

        return DetailsUserHistoryModuleDto
                .builder()
                .noSets(userHistoryModule.getNoSets())
                .build();
    }

    public DetailsUserHistoryExerciseDto getUserHistoryExerciseDetails(Long userHistoryExerciseId) throws NotFoundException {
        UserHistoryExercise userHistoryExercise = userHistoryExerciseService.findById(userHistoryExerciseId);

        return DetailsUserHistoryExerciseDto
                .builder()
                .noReps(userHistoryExercise.getNoReps())
                .currentNoSeconds(userHistoryExercise.getCurrNoSeconds())
                .weight(userHistoryExercise.getWeight())
                .build();
    }

    public Set<SimplifiedExerciseDto> getAllNonExclusiveExercisesByName(String exerciseName) {

        return exerciseService.getAllNonExclusiveExercisesByName(exerciseName)
                .stream()
                .map(exercise -> SimplifiedExerciseDto
                                .builder()
                                .idExercise(exercise.getId())
                                .name(exercise.getName())
                                .exerciseImageStartUrl(exercise.getExerciseImageStartUrl())
//                        .equipmentName(exercise.getEquipment().getName())
                                .muscleGroupName(exercise.getMuscleGroup().getName())
                                .difficultyName(exercise.getDifficulty().getDificultyLevel())
                                .categoryName(exercise.getCategory().getName())
                                .build()
                ).collect(Collectors.toSet());
    }

    @Cacheable(value = "longPeriodCache", key = "'getAllNonExclusiveExercises'")
    public Set<SimplifiedExerciseDto> getAllNonExclusiveExercises() {
        return exerciseService.getAllNonExclusiveExercises()
                .stream()
                .map(exercise -> SimplifiedExerciseDto
                                .builder()
                                .idExercise(exercise.getId())
                                .name(exercise.getName())
                                .exerciseImageStartUrl(exercise.getExerciseImageStartUrl())
//                        .equipmentName(exercise.getEquipment().getName())
                                .muscleGroupName(exercise.getMuscleGroup().getName())
                                .difficultyName(exercise.getDifficulty().getDificultyLevel())
                                .categoryName(exercise.getCategory().getName())
                                .build()
                ).collect(Collectors.toSet());
    }


    public void createUserWorkout(CreateUserWorkoutDto createUserWorkoutDto) throws NotFoundException {

        Set<Exercise> exercises = createUserWorkoutDto.getExercisesIds().stream().map(exerciseService::getExerciseById).collect(Collectors.toSet());

        workoutService.saveWorkout(
                Workout
                        .builder()
                        .name(createUserWorkoutDto.getWorkoutName())
                        .description(createUserWorkoutDto.getDescription())
                        .exercises(exercises)
                        .adder(authorityService.getUser())
                        .isDeleted(false)
                        .isGlobal(false)
                        .difficultyLevel(calculateDifficultyLevel(exercises))
                        .build()

        );
    }

    public List<WorkoutDto> getPersonalWorkouts() {

        return workoutService.getAllWorkouts()
                .stream()
                .filter(workout -> {
                    try {
                        return authorityService.getUser().equals(workout.getAdder());
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(
                        workout ->
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

    public LastWorkoutResultDto getLastWorkoutStatistics() {

        WorkoutResult workoutResult = workoutResultService.findLastByUser(authorityService.getEmail());
        if (workoutResult == null)
            throw new LastWorkoutResultEmpty("there is no last workout result entry for this user");

        return LastWorkoutResultDto
                .builder()
                .id(workoutResult.getId())
                .totalTime(workoutResult.getTotalTime())
                .totalCaloriesBurned(workoutResult.getTotalCaloriesBurned())
                .totalVolume(workoutResult.getTotalVolume())
                .resultCategoryPercentages(workoutResult.getResultCategoryPercentages())
                .resultDifficultyPercentages(workoutResult.getResultDifficultyPercentages())
                .resultMuscleGroupPercentages(workoutResult.getResultMuscleGroupPercentages())
                .build();
    }


    public List<LastWorkoutResultDto> getGeneralWorkoutInformation(Integer noWorkoutResults) {
        return workoutResultService.
                findLastEntriesByUser(authorityService.getEmail(), noWorkoutResults)
                .stream()
                .map(
                        workoutResult ->
                                LastWorkoutResultDto
                                        .builder()
                                        .id(workoutResult.getId())
                                        .totalTime(workoutResult.getTotalTime())
                                        .totalCaloriesBurned(workoutResult.getTotalCaloriesBurned())
                                        .totalVolume(workoutResult.getTotalVolume())
                                        .resultCategoryPercentages(workoutResult.getResultCategoryPercentages())
                                        .resultDifficultyPercentages(workoutResult.getResultDifficultyPercentages())
                                        .resultMuscleGroupPercentages(workoutResult.getResultMuscleGroupPercentages())
                                        .build()
                ).toList();
    }
}
