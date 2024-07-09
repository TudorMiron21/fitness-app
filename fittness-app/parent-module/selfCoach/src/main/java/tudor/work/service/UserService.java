package tudor.work.service;

import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tudor.work.dto.*;
import tudor.work.exceptions.*;
import tudor.work.model.*;
import tudor.work.repository.ExerciseRepository;
import tudor.work.repository.UserRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
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
    private final UserHistoryExerciseService userHistoryExerciseService;
    private final WorkoutResultService workoutResultService;
    private final StatisticsService statisticsService;
    private final PersonalRecordService personalRecordService;
    private final LeaderBoardService leaderBoardService;
    private final AchievementService achievementService;
    private final MinioService minioService;
    private final ProgramService programService;


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

    public List<WorkoutDto> getHomePageWorkouts() throws NotFoundException {
        List<Workout> allWorkouts = workoutService.getAllWorkouts();

        if (authorityService.isUser()) {
            return allWorkouts
                    .stream()
                    .filter(Workout::isGlobal)
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
                    ).sorted(Comparator.comparing(WorkoutDto::getNoLikes))
                    .toList();
        }
        else
            if (authorityService.isPayingUser()) {
            User payingUser = authorityService.getUser();
            Predicate<Workout> isWorkoutAddedByFollowingCoach = workout -> {
                return payingUser.getFollowing().contains(workout.getAdder());
            };

            Predicate<Workout> isWorkoutAddedByAdmin = Workout::isGlobal;

            return allWorkouts
                    .stream()
                    .filter(isWorkoutAddedByFollowingCoach.or(isWorkoutAddedByAdmin))
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
                    ).sorted(Comparator.comparing(WorkoutDto::getNoLikes))
                    .toList();
        }
        return new ArrayList<>();
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
                .caloriesBurned(requestUserHistoryExercise.getCaloriesBurned())
                .isDone(requestUserHistoryExercise.isDone())
                .build();


        if (requestUserHistoryExercise.getExercise().isHasNoReps() || requestUserHistoryExercise.getExercise().isHasWeight()) {

            //this is for the exercises that only have number of reps
            if (requestUserHistoryExercise.getExercise().isHasNoReps()) {
                userHistoryExercise.setNoReps(requestUserHistoryExercise.getNoReps());

                if (!requestUserHistoryExercise.getExercise().isHasWeight()) {

                    Optional<PersonalRecord> personalRecord = personalRecordService.findByUserIdAndExerciseId(authorityService.getUserId(), requestUserHistoryExercise.getExercise().getId());

                    //if the exercise is already present in the personal record table for this user
                    if (personalRecord.isPresent()) {
                        PersonalRecord personalRecordValue = personalRecord.get();

                        if (personalRecordValue.getMaxNoReps() != null) {

                            if (personalRecordValue.getMaxNoReps() < requestUserHistoryExercise.getNoReps()) {
                                personalRecordValue.setMaxNoReps(requestUserHistoryExercise.getNoReps());
                                personalRecordValue.setMaxTime(requestUserHistoryExercise.getCurrNoSeconds());
//                                personalRecordValue.setUserHistoryExercise(userHistoryExercise);
                            }
                        } else {
                            personalRecordValue.setMaxNoReps(requestUserHistoryExercise.getNoReps());
                            personalRecordValue.setMaxTime(requestUserHistoryExercise.getCurrNoSeconds());
//                            personalRecordValue.setUserHistoryExercise(userHistoryExercise);

                        }
                    } else {

                        personalRecordService.save(
                                PersonalRecord
                                        .builder()
                                        .exercise(requestUserHistoryExercise.getExercise())
                                        .user(authorityService.getUser())
                                        .maxNoReps(requestUserHistoryExercise.getNoReps())
                                        .maxTime(requestUserHistoryExercise.getCurrNoSeconds())
//                                        .userHistoryExercise(userHistoryExercise)

                                        .build()
                        );
                    }
                }

            }

            if (requestUserHistoryExercise.getExercise().isHasWeight()) {
                userHistoryExercise.setWeight(requestUserHistoryExercise.getWeight());

                Optional<PersonalRecord> personalRecord = personalRecordService.findByUserIdAndExerciseId(authorityService.getUserId(), requestUserHistoryExercise.getExercise().getId());

                if (personalRecord.isPresent()) {
                    PersonalRecord personalRecordValue = personalRecord.get();

                    if (personalRecordValue.getMaxWeight() != null) {

                        if (personalRecordValue.getMaxWeight() < requestUserHistoryExercise.getWeight()) {
                            personalRecordValue.setMaxWeight(requestUserHistoryExercise.getWeight());
                            personalRecordValue.setMaxNoReps(requestUserHistoryExercise.getNoReps());
                            personalRecordValue.setMaxTime(requestUserHistoryExercise.getCurrNoSeconds());
//                            personalRecordValue.setUserHistoryExercise(userHistoryExercise);

                        }
                    } else {
                        personalRecordValue.setMaxWeight(requestUserHistoryExercise.getWeight());
                        personalRecordValue.setMaxNoReps(requestUserHistoryExercise.getNoReps());
                        personalRecordValue.setMaxTime(requestUserHistoryExercise.getCurrNoSeconds());
//                        personalRecordValue.setUserHistoryExercise(userHistoryExercise);

                    }
                } else {

                    personalRecordService.save(
                            PersonalRecord
                                    .builder()
                                    .exercise(requestUserHistoryExercise.getExercise())
                                    .user(authorityService.getUser())
                                    .maxWeight(requestUserHistoryExercise.getWeight())
                                    .maxTime(requestUserHistoryExercise.getCurrNoSeconds())
//                                    .userHistoryExercise(userHistoryExercise)
                                    .build()
                    );
                }
            }

            if (requestUserHistoryExercise.getExercise().isHasNoReps() && requestUserHistoryExercise.getExercise().isHasWeight()) {
                Optional<PersonalRecord> personalRecord = personalRecordService.findByUserIdAndExerciseId(authorityService.getUserId(), requestUserHistoryExercise.getExercise().getId());


                if (personalRecord.isPresent()) {

                    PersonalRecord personalRecordValue = personalRecord.get();

                    if (personalRecordValue.getMaxVolume() != null) {

                        if (personalRecordValue.getMaxVolume() < requestUserHistoryExercise.getWeight() * requestUserHistoryExercise.getNoReps()) {
                            personalRecordValue.setMaxVolume(requestUserHistoryExercise.getWeight() * requestUserHistoryExercise.getNoReps());
                            personalRecordValue.setMaxTime(requestUserHistoryExercise.getCurrNoSeconds());
//                            personalRecordValue.setUserHistoryExercise(userHistoryExercise);
                        }
                    } else {
                        personalRecordValue.setMaxVolume(requestUserHistoryExercise.getWeight() * requestUserHistoryExercise.getNoReps());
                        personalRecordValue.setMaxTime(requestUserHistoryExercise.getCurrNoSeconds());
//                        personalRecordValue.setUserHistoryExercise(userHistoryExercise);
                    }
                } else {
                    personalRecordService.save(
                            PersonalRecord
                                    .builder()
                                    .exercise(requestUserHistoryExercise.getExercise())
                                    .user(authorityService.getUser())
                                    .maxVolume(requestUserHistoryExercise.getWeight() * requestUserHistoryExercise.getNoReps())
                                    .maxTime(requestUserHistoryExercise.getCurrNoSeconds())
//                                    .userHistoryExercise(userHistoryExercise)
                                    .build()
                    );
                }
            }

        } else {
            userHistoryExercise.setCaloriesBurned(requestUserHistoryExercise.getCaloriesBurned());

            Optional<PersonalRecord> personalRecord = personalRecordService.findByUserIdAndExerciseId(authorityService.getUserId(), requestUserHistoryExercise.getExercise().getId());

            if (personalRecord.isPresent()) {
                PersonalRecord personalRecordValue = personalRecord.get();

                if (personalRecordValue.getMaxCalories() != null) {
                    if (personalRecordValue.getMaxCalories() < requestUserHistoryExercise.getCaloriesBurned()) {
                        personalRecordValue.setMaxCalories(requestUserHistoryExercise.getCaloriesBurned());
                        personalRecordValue.setMaxTime(requestUserHistoryExercise.getCurrNoSeconds());
//                        personalRecordValue.setUserHistoryExercise(userHistoryExercise);

                    }
                } else {
                    personalRecordValue.setMaxCalories(requestUserHistoryExercise.getCaloriesBurned());
                    personalRecordValue.setMaxTime(requestUserHistoryExercise.getCurrNoSeconds());
//                    personalRecordValue.setUserHistoryExercise(userHistoryExercise);
                }
            } else {

                personalRecordService.save(
                        PersonalRecord
                                .builder()
                                .exercise(requestUserHistoryExercise.getExercise())
                                .user(authorityService.getUser())
                                .maxCalories(requestUserHistoryExercise.getCaloriesBurned())
                                .maxTime(requestUserHistoryExercise.getCurrNoSeconds())
//                                .userHistoryExercise(userHistoryExercise)
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


    @Transactional
    public void incrementWorkoutIndex(Long userHistoryWorkoutId) throws NotFoundException {
        UserHistoryWorkout userHistoryWorkout = userHistoryWorkoutService.findById(userHistoryWorkoutId);


        UserHistoryProgram userHistoryProgram = userHistoryWorkout.getUserHistoryProgram();
        if (userHistoryProgram != null) {
            userHistoryProgram.setCurrentWorkoutIndex(
                    userHistoryProgram
                            .getProgram()
                            .getNextGreatestIndex(
                                    userHistoryProgram
                                            .getCurrentWorkoutIndex()
                            )
            );

            if (userHistoryProgram.getCurrentWorkoutIndex() == -1) {
                userHistoryProgram.setIsProgramDone(true);
                userHistoryProgram.setFinishedWorkoutDateAndTime(LocalDateTime.now());
            }
        }
    }


//    private Boolean getPersonalRecordForUserHistoryExercise(UserHistoryExercise userHistoryExercise) {
//        Optional<PersonalRecord> personalRecordOptional = personalRecordService.findByUserHistoryExercise(userHistoryExercise);
//        return personalRecordOptional.isPresent();
//    }


    @Transactional
    public WorkoutRewardsResponseDto addUserResults(Long userHistoryWorkoutId) throws NotFoundException {

        UserHistoryWorkout userHistoryWorkout = userHistoryWorkoutService.findById(userHistoryWorkoutId);

        Double noPoints = this.getNumberOfPoints(userHistoryWorkout);

        Integer noDoneExercises = this.getNoDoneExercises(userHistoryWorkout);
        Integer noDoneWorkouts = 1;
        Integer noDonePrograms = this.getNoDonePrograms(userHistoryWorkout);


        Optional<LeaderBoard> leaderBoardOptional = leaderBoardService.findByUser(authorityService.getUser());

        WorkoutRewardsResponseDto workoutRewardsResponseDto;
        if (leaderBoardOptional.isPresent()) {

            LeaderBoard leaderBoard = leaderBoardOptional.get();

            workoutRewardsResponseDto = addAchievements(
                    leaderBoard.getNumberOfDoneExercises(), noDoneExercises,
                    leaderBoard.getNumberOfDoneWorkouts(), noDoneWorkouts,
                    leaderBoard.getNumberOfDonePrograms(), noDonePrograms
            );
            noPoints += workoutRewardsResponseDto.getNumberOfPoints();

            leaderBoard.setNumberOfPoints(leaderBoard.getNumberOfPoints() + noPoints);
            leaderBoard.setNumberOfDoneExercises(leaderBoard.getNumberOfDoneExercises() + noDoneExercises);
            leaderBoard.setNumberOfDoneWorkouts(leaderBoard.getNumberOfDoneWorkouts() + noDoneWorkouts);
            leaderBoard.setNumberOfDonePrograms(leaderBoard.getNumberOfDonePrograms() + noDonePrograms);

            workoutRewardsResponseDto.setNumberOfPoints(workoutRewardsResponseDto.getNumberOfPoints() + noPoints);

        } else {

            workoutRewardsResponseDto = addAchievements(
                    0, noDoneExercises,
                    0, noDoneWorkouts,
                    0, noDonePrograms
            );
            noPoints += workoutRewardsResponseDto.getNumberOfPoints();
            LeaderBoard leaderBoard =
                    LeaderBoard
                            .builder()
                            .user(authorityService.getUser())
                            .numberOfPoints(noPoints)
                            .numberOfDoneExercises(noDoneExercises)
                            .numberOfDoneWorkouts(noDoneWorkouts)
                            .numberOfDonePrograms(noDonePrograms)
                            .build();
            leaderBoardService.save(leaderBoard);
            workoutRewardsResponseDto.setNumberOfPoints(workoutRewardsResponseDto.getNumberOfPoints() + noPoints);

        }


        //converts the picture from path to pre signed url
        workoutRewardsResponseDto.getAchievements().forEach(achievementDto -> {
            try {
                achievementDto.setAchievementPicturePath(
                        minioService.generatePreSignedUrl(achievementDto.getAchievementPicturePath())
                );
            } catch (ServerException | InsufficientDataException | ErrorResponseException |
                     IOException | NoSuchAlgorithmException | InvalidKeyException |
                     InvalidResponseException | XmlParserException | InternalException e) {
                throw new RuntimeException(e);
            }
        });
        return workoutRewardsResponseDto;

    }

    private Double getNumberOfPoints(UserHistoryWorkout userHistoryWorkout) {
        //pondere = p = 0.1;
        //exercise = e
        //no_points = sum(p * volume(e) + p/2 * time(e) + p * no_calories(e) )
        //if a personal record was set doing the exercise, then p = p * 2;
        List<UserHistoryExercise> userHistoryExercises = userHistoryWorkout.getUserHistoryModules()
                .stream()
                .flatMap(module -> module.getUserHistoryExercises().stream())
                .toList();

        Double weight = 0.1; //pondere
        return userHistoryExercises
                .stream()
                .mapToDouble(
                        uhe ->
                        {
                            Double numberOfPoints;
                            Double caloriesBurned = 0.0;
                            Double exerciseWeight = 0.0;
                            Integer noReps = 0;
                            Long exerciseTime = 0L;

                            if (uhe.getCaloriesBurned() != null)
                                caloriesBurned = uhe.getCaloriesBurned();

                            if (uhe.getWeight() != null)
                                exerciseWeight = uhe.getWeight();

                            if (uhe.getNoReps() != null)
                                noReps = uhe.getNoReps();

                            if (uhe.getCurrNoSeconds() != null)
                                exerciseTime = uhe.getCurrNoSeconds();

                            numberOfPoints = exerciseWeight * noReps * weight + exerciseTime * weight / 2 + caloriesBurned * weight;
//                            if (this.getPersonalRecordForUserHistoryExercise(uhe))
//                                numberOfPoints *= 2;
                            return numberOfPoints;
                        }
                )
                .sum();
    }

    //this returns the number of point accumulated through the achievements
    private WorkoutRewardsResponseDto addAchievements(
            Integer totalNumberOfDoneExercises, Integer noDoneExercises,
            Integer totalNumberOfDoneWorkouts, Integer noDoneWorkouts,
            Integer totalNumberOfDonePrograms, Integer noDonePrograms) throws NotFoundException {

        List<Achievement> allAchievements = achievementService.findAll();

        Set<Achievement> achievementsClaimed =
                allAchievements
                        .stream()
                        .filter(
                                achievement -> {
                                    return ((achievement.getNumberOfExercisesMilestone() > totalNumberOfDoneExercises) && (achievement.getNumberOfExercisesMilestone() <= totalNumberOfDoneExercises + noDoneExercises)) ||
                                            ((achievement.getNumberOfWorkoutsMileStone() > totalNumberOfDoneWorkouts) && (achievement.getNumberOfWorkoutsMileStone() <= totalNumberOfDoneWorkouts + noDoneWorkouts)) ||
                                            ((achievement.getNumberOfProgramsMilestone() > totalNumberOfDonePrograms) && (achievement.getNumberOfProgramsMilestone() <= totalNumberOfDonePrograms + noDonePrograms));
                                }
                        ).collect(Collectors.toSet());

        User user = authorityService.getUser();

        Set<Achievement> userAchievements = user.getAchievements();

        userAchievements.addAll(achievementsClaimed);

        user.setAchievements(userAchievements);

        return WorkoutRewardsResponseDto
                .builder()
                .numberOfPoints(
                        achievementsClaimed
                                .stream()
                                .mapToDouble(Achievement::getNumberOfPoints)
                                .sum()
                )
                .achievements(
                        achievementsClaimed
                                .stream()
                                .map(achievement ->
                                        AchievementDto
                                                .builder()
                                                .id(achievement.getId())
                                                .name(achievement.getName())
                                                .description(achievement.getDescription())
                                                .numberOfPoints(achievement.getNumberOfPoints())
                                                .achievementPicturePath(achievement.getAchievementPicturePath())
                                                .build()
                                ).collect(Collectors.toSet())
                )
                .build();
    }

    private Integer getNoDoneExercises(UserHistoryWorkout userHistoryWorkout) {

        return userHistoryWorkout.getUserHistoryModules()
                .size();
    }


    private Integer getNoDonePrograms(UserHistoryWorkout userHistoryWorkout) {
        UserHistoryProgram userHistoryProgram = userHistoryWorkout.getUserHistoryProgram();
        if (userHistoryProgram != null)
            if (userHistoryProgram.getIsProgramDone())
                return 1;
        return 0;
    }


    public WorkoutRewardsResponseDto finishWorkout(Long userHistoryWorkoutId) throws NotFoundException, ExecutionException, InterruptedException {

        WorkoutResult workoutResult = saveUserHistoryWorkoutAndGetWorkoutResult(userHistoryWorkoutId);

        incrementWorkoutIndex(userHistoryWorkoutId);

        WorkoutRewardsResponseDto workoutRewardsResponseDto = addUserResults(userHistoryWorkoutId);

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

        return workoutRewardsResponseDto;
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

    //    @Cacheable(value = "longPeriodCache", key = "'getAllNonExclusiveExercises'")
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


    public Set<PersonalRecordDto> getPersonalRecordsForUser() throws NotFoundException {

        return personalRecordService
                .getPersonalRecordsForUser(authorityService.getUserId())
                .stream()
                .map(
                        personalRecord
                                ->
                                PersonalRecordDto
                                        .builder()
                                        .id(personalRecord.getId())
                                        .exercise(personalRecord.getExercise())
                                        .maxWeight(personalRecord.getMaxWeight())
                                        .maxTime(personalRecord.getMaxTime())
                                        .maxCalories(personalRecord.getMaxCalories())
                                        .maxNoReps(personalRecord.getMaxNoReps())
                                        .maxVolume(personalRecord.getMaxVolume())
                                        .build()
                ).collect(Collectors.toSet());
    }

    public List<LeaderBoardDto> getLeaderBoardEntries() {
        List<LeaderBoard> leaderBoards = leaderBoardService.findAll();

        return
                leaderBoards
                        .stream()
                        .map(leaderBoard ->
                                LeaderBoardDto
                                        .builder()
                                        .id(leaderBoard.getId())
                                        .numberOfPoints(leaderBoard.getNumberOfPoints())
                                        .numberOfDoneExercises(leaderBoard.getNumberOfDoneExercises())
                                        .numberOfDoneWorkouts(leaderBoard.getNumberOfDoneWorkouts())
                                        .numberOfDonePrograms(leaderBoard.getNumberOfDonePrograms())
                                        .user(
                                                UserDto
                                                        .builder()
                                                        .id(leaderBoard.getUser().getId())
                                                        .email(leaderBoard.getUser().getEmail())
                                                        .firstName(leaderBoard.getUser().getFirstname())
                                                        .lastName(leaderBoard.getUser().getLastname())
                                                        .profilePictureUrl("profile-pic")
                                                        .build()
                                        )
                                        .build()
                        ).toList();
    }

    public User findByEmail(String email) throws NotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("user with email " + email + " not found"));
    }

    public List<LeaderBoardDto> getContactsLeaderBoard(List<String> emails) {
        return emails.stream().map(email -> {
                    try {
                        return findByEmail(email);
                    } catch (NotFoundException nfe) {
                        System.out.println(nfe.getMessage());
                        return null; // Return null when user is not found
                    }
                }).filter(Objects::nonNull) // Filter out null users
                .map(user -> {
                    Optional<LeaderBoard> leaderBoardOptional = leaderBoardService.findByUser(user);
                    if (leaderBoardOptional.isPresent()) {
                        LeaderBoard leaderBoard = leaderBoardOptional.get();
                        return LeaderBoardDto.builder()
                                .id(leaderBoard.getId())
                                .numberOfPoints(leaderBoard.getNumberOfPoints())
                                .numberOfDoneExercises(leaderBoard.getNumberOfDoneExercises())
                                .numberOfDoneWorkouts(leaderBoard.getNumberOfDoneWorkouts())
                                .numberOfDonePrograms(leaderBoard.getNumberOfDonePrograms())
                                .user(UserDto.builder()
                                        .id(leaderBoard.getUser().getId())
                                        .email(leaderBoard.getUser().getEmail())
                                        .firstName(leaderBoard.getUser().getFirstname())
                                        .lastName(leaderBoard.getUser().getLastname())
                                        .profilePictureUrl("profile-pic")
                                        .build())
                                .build();
                    } else {
                        // Handle the case when leaderboard is not found
                        System.out.println("Leaderboard not found for user: " + user.getEmail());
                        return null; // Return null when leaderboard is not found
                    }
                }).filter(Objects::nonNull) // Filter out null leaderboards
                .toList();
    }

    public List<WorkoutDto> getFilteredWorkouts(FilterSearchDto filterSearchDto) throws NotFoundException {
        //get filtered workouts
        List<Workout> filteredWorkouts = workoutService.getFilteredWorkouts(filterSearchDto);

        //get admin workouts
        Predicate<Workout> isWorkoutGlobal = Workout::isGlobal;

        //get following coaches
        Set<User> followingCoaches = authorityService.getUser().getFollowing();

        //get following coaches workouts
        Predicate<Workout> isWorkoutFromFollowingCoaches =
                workout ->
                {
                    if (authorityService.isUser()) {
                        return false;
                    } else if (authorityService.isPayingUser()) {
                        if (followingCoaches.contains(workout.getAdder())) {
                            return true;
                        }
                    }
                    return false;
                };

        return filteredWorkouts
                .stream()
                .filter(
                        isWorkoutGlobal
                                .or(isWorkoutFromFollowingCoaches))
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
                ).toList();
    }

    public List<ProgramDto> getFilteredPrograms(FilterSearchDto filterSearchDto) throws NotFoundException {

        //get filtered programs
        List<Program> filteredPrograms = programService.getFilteredPrograms(filterSearchDto);

        //filter admin programs
        Predicate<Program> isAdminProgram = program -> program.getAdder().getRole().equals(Roles.ADMIN);

        //get following coaches list
        Set<User> followingCoaches = authorityService.getUser().getFollowing();

        //filter following coaches programs
        Predicate<Program> isProgramFromFollowingCoaches =
                program ->
                {
                    if (authorityService.isUser())
                        return false;
                    else if (authorityService.isPayingUser()) {
                        if (followingCoaches.contains(program.getAdder())) {
                            return true;
                        }
                    }
                    return false;
                };

        return filteredPrograms
                .stream()
                .filter(isAdminProgram.or(isProgramFromFollowingCoaches))
                .map(
                        program ->
                                ProgramDto
                                        .builder()
                                        .id(program.getId())
                                        .name(program.getName())
                                        .description(program.getDescription())
                                        .durationInDays(program.getDurationInDays())
                                        .coverPhotoUrl(program.getCoverPhotoUrl())
                                        .workoutProgramSet(program.getWorkoutPrograms())
                                        .build()
                )
                .toList();

    }

    public List<AchievementDto> getUserAchievements() throws NotFoundException {
        return authorityService.getUser().getAchievements().stream()
                .map(
                        achievement ->
                        {
                            try {
                                return AchievementDto
                                        .builder()
                                        .id(achievement.getId())
                                        .name(achievement.getName())
                                        .description(achievement.getDescription())
                                        .numberOfPoints(achievement.getNumberOfPoints())
                                        .achievementPicturePath(minioService.generatePreSignedUrl(achievement.getAchievementPicturePath()))
                                        .build();
                            } catch (ServerException | InsufficientDataException | ErrorResponseException |
                                     IOException | NoSuchAlgorithmException | InvalidKeyException |
                                     InvalidResponseException | XmlParserException | InternalException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).toList();
    }

    public LeaderBoardDto getLeaderBoardEntryForUser() throws NotFoundException, LeaderBoardEntryNotFoundException {
        Optional<LeaderBoard> optionalLeaderBoard = leaderBoardService.findByUser(authorityService.getUser());

        if (optionalLeaderBoard.isPresent()) {

            LeaderBoard leaderBoard = optionalLeaderBoard.get();
            return LeaderBoardDto.builder()
                    .id(leaderBoard.getId())
                    .numberOfPoints(leaderBoard.getNumberOfPoints())
                    .numberOfDoneExercises(leaderBoard.getNumberOfDoneExercises())
                    .numberOfDoneWorkouts(leaderBoard.getNumberOfDoneWorkouts())
                    .numberOfDonePrograms(leaderBoard.getNumberOfDonePrograms())
                    .user(UserDto.builder()
                            .id(leaderBoard.getUser().getId())
                            .email(leaderBoard.getUser().getEmail())
                            .firstName(leaderBoard.getUser().getFirstname())
                            .lastName(leaderBoard.getUser().getLastname())
                            .profilePictureUrl("profile-pic")
                            .build())
                    .build();
        } else {
            throw new LeaderBoardEntryNotFoundException("leader board entry for user with id " + authorityService.getUser().getId() + " not found");
        }
    }

    public User upgradeUserRole(String email) throws NotFoundException {
        User user = this.getUserByEmail(email);
        user.setRole(Roles.PAYING_USER);
        return user;
    }

    @Transactional
    public Long webhookResponse(Map<String, Object> requestBody) {
        String email = (String) ((Map<String, Object>) ((Map<String, Object>) requestBody.get("resource")).get("subscriber")).get("email_address");
        String eventType = (String) requestBody.get("event_type");

        if (eventType.equals("BILLING.SUBSCRIPTION.ACTIVATED")) {
            try {
                User user = this.upgradeUserRole(email);
                return user.getId();
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("event type " + eventType + " is NOT \"BILLING.SUBSCRIPTION.ACTIVATED\"");
        }
    }

    @Transactional
    public void uploadUserDetails(Gender gender, Integer age, Integer height, Double currentWeight, Double goalWeight) throws NotFoundException {

        User user = authorityService.getUser();

        user.setGender(gender);
        user.setAge(age);
        user.setHeight(height);
        user.setCurrentWeight(currentWeight);
        user.setGoalWeight(goalWeight);
    }

    public UserDetailsResponseDto getUserDetails() throws NotFoundException {

        User user = authorityService.getUser();
        return
                UserDetailsResponseDto
                        .builder()
                        .gender(user.getGender())
                        .age(user.getAge())
                        .height(user.getHeight())
                        .weight(user.getCurrentWeight())
                        .build();
    }


    @Transactional
    public void uploadProfilePicture(MultipartFile file) throws IOException, NotFoundException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        User user = authorityService.getUser();

        String objectName = user.getId().toString();

        if(minioService.objectExists(objectName,"user-profile-picture"))
             minioService.deleteObject(objectName,"user-profile-picture");

        minioService.uploadImageToObjectStorage(file.getInputStream(),objectName,"user-profile-picture");

        String imgPath = "user-profile-picture/" + objectName;

        user.setProfilePictureLocation(imgPath);
    }

    public String getProfilePicture() throws NotFoundException, ServerException, InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, ErrorResponseException {
        User user = authorityService.getUser();
        String objectName = user.getId().toString();

        if(minioService.objectExists(objectName,"user-profile-picture"))
            return minioService.generatePreSignedUrl(user.getProfilePictureLocation());
        else
            throw new NotFoundException("user has no profile picture");
    }

    public Set<User> getAllByRole(Roles role){
        return userRepository.findAllByRole(role);
    }
}
