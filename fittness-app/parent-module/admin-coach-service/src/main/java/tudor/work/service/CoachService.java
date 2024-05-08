package tudor.work.service;


import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tudor.work.dto.*;
import tudor.work.model.*;
import tudor.work.utils.MinioMultipartUploadUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachService {

    private final MinioService minioService;
    private final CoachDetailsService coachDetailsService;
    private final AuthorityService authorityService;
    private final MinioMultipartUploadUtils minioMultipartUploadUtils;
    private final ExerciseService exerciseService;
    private final EquipmentService equipmentService;
    private final MuscleGroupService muscleGroupService;
    private final DifficultyService difficultyService;
    private final CategoryService categoryService;
    private final WorkoutService workoutService;
    private final ProgramService programService;
    private final UserService userService;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;


    public void uploadCoachDetails(UploadCoachDetailsRequestDto uploadCoachDetailsRequestDto) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, NotFoundException {

        User user = authorityService.getUser();

        String imgFileName = user.getId() + ".png";

        String imgPath = minioService.uploadImageToObjectStorage(uploadCoachDetailsRequestDto.getCertificateImg().getInputStream(), imgFileName, "certificates");

        coachDetailsService.save(
                CoachDetails
                        .builder()
                        .user(user)
                        .coachCertificatePath(imgPath)
                        .certificationType(uploadCoachDetailsRequestDto.getCertificationType())
                        .yearsOfExperience(uploadCoachDetailsRequestDto.getYearsOfExperience())
//                        .isValidated(false)
                        .build()
        );
    }

    public Boolean checkAreCoachDetailsValid() throws NotFoundException {

        // Convert null to false
        return authorityService.getUser()
                .getCoachDetails()
                .stream()
                .anyMatch(cd -> Optional.ofNullable(cd.getIsValidated()).orElse(false));    }

    private Integer generatePartCount(Long fileSize) {
        long maxRequestSizeLong = Long.parseLong(maxRequestSize.replaceAll("[^0-9]", "")) * 1000000;
        return (int) ((fileSize + maxRequestSizeLong - 1) / maxRequestSizeLong);
    }

    public String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == 0) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }

    @Transactional
    public Map<String, Object> UploadExerciseDetailsAndInitMultipart(String bucketName, String contentType, UploadExerciseDetailsDto uploadExerciseDetailsDto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, NotFoundException//also add the UploadExerciseDto as a parameter
    {

        boolean hasReps = true;
        boolean hasWeight = true;
        if (uploadExerciseDetailsDto.getCategoryName().equals("Cardio")) {

            hasReps = false;
            hasWeight = false;
        }

        if (uploadExerciseDetailsDto.getEquipmentName().equals("Body Only")) {
            hasWeight = false;
        }

        Exercise savedExercise = exerciseService.saveExercise(
                Exercise.
                        builder()
                        .name(uploadExerciseDetailsDto.getExerciseName())
                        .description(uploadExerciseDetailsDto.getDescription())
                        .equipment(equipmentService.getEquipmentByName(uploadExerciseDetailsDto.getEquipmentName()))
                        .muscleGroup(muscleGroupService.getMuscleGroupByName(uploadExerciseDetailsDto.getMuscleGroupName()))
                        .difficulty(difficultyService.getDifficultyByName(uploadExerciseDetailsDto.getDifficultyName()))
                        .category(categoryService.getCategoryByName(uploadExerciseDetailsDto.getCategoryName()))
                        .adder(authorityService.getUser())
                        .isExerciseExclusive(true)
                        .hasWeight(hasWeight)
                        .hasNoReps(hasReps)
                        .build()
        );

        if (!uploadExerciseDetailsDto.getBeforeImage().isEmpty()) {
            minioService.createBucket("exercise-images");
            minioService.uploadImageToObjectStorage(
                    uploadExerciseDetailsDto.getBeforeImage().getInputStream(),
                    savedExercise.getId() + "_before." + this.getFileExtension(uploadExerciseDetailsDto.getBeforeImage().getOriginalFilename()),
                    "exercise-images"
            );
            String pathBeforeImage = "exercise-images/" + savedExercise.getId() + "_before." + this.getFileExtension(uploadExerciseDetailsDto.getBeforeImage().getOriginalFilename());
            savedExercise.setExerciseImageStartUrl(pathBeforeImage);
        }
        if (!uploadExerciseDetailsDto.getAfterImage().isEmpty()) {
            minioService.createBucket("exercise-images");
            minioService.uploadImageToObjectStorage(
                    uploadExerciseDetailsDto.getAfterImage().getInputStream(),
                    savedExercise.getId() + "_after." + this.getFileExtension(uploadExerciseDetailsDto.getAfterImage().getOriginalFilename()),
                    "exercise-images"
            );
            String pathAfterImage = "exercise-images/" + savedExercise.getId() + "_after." + this.getFileExtension(uploadExerciseDetailsDto.getAfterImage().getOriginalFilename());
            savedExercise.setExerciseImageEndUrl(pathAfterImage);
        }
        if (uploadExerciseDetailsDto.getVideoSize() > 0) {
            minioService.createBucket(bucketName);

            Map<String, Object> response = minioMultipartUploadUtils
                    .initMultipartUpload(bucketName,
                            savedExercise.getId() + "." + getFileExtension(uploadExerciseDetailsDto.getVideoName()),
                            generatePartCount(uploadExerciseDetailsDto.getVideoSize()), contentType);

            response.put("exerciseId", savedExercise.getId());

            return response;
        }
        return new HashMap<>();
    }


    @Transactional
    public Boolean completeMultipartUpload(String bucketName, CompleteMultipartUploadDto completeMultipartUploadDto) throws NotFoundException {


        Exercise uploadExercise = exerciseService.getExerciseByid(completeMultipartUploadDto.getExerciseId());

        String objectName = uploadExercise.getId() + "." + getFileExtension(completeMultipartUploadDto.getFilename());
        String videoPath = bucketName + "/" + objectName;

        uploadExercise.setExerciseVideoUrl(videoPath);

        return minioMultipartUploadUtils.mergeMultipartUploads(bucketName, objectName, completeMultipartUploadDto.getUploadId());
    }


    public Set<ExerciseResponseDto> getFilteredExercises(ExerciseFilteredRequestDto exerciseFilteredRequestDto) throws NotFoundException {
        Set<Exercise> exercises = exerciseService.getFilteredExercises(exerciseFilteredRequestDto);

        if (exerciseFilteredRequestDto.getIsExercisePrivate()) {
            exercises.stream().filter(exercise -> {
                try {
                    return exercise.getAdder().equals(authorityService.getUser());
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).forEach(exercise -> {
                try {
                    exercise.setExerciseImageStartUrl(minioService.generatePreSignedUrl(exercise.getExerciseImageStartUrl()));
                } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                         NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                         XmlParserException |
                         InternalException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return exercises.stream().map(exercise ->
                ExerciseResponseDto
                        .builder()
                        .exerciseId(exercise.getId())
                        .name(exercise.getName())
                        .exerciseImageStartUrl(exercise.getExerciseImageStartUrl())
                        .muscleGroup(exercise.getMuscleGroup())
                        .equipment(exercise.getEquipment())
                        .difficulty(exercise.getDifficulty())
                        .category(exercise.getCategory())
                        .isExerciseExclusive(exercise.isExerciseExclusive())
                        .build()
        ).collect(Collectors.toSet());
    }

    private Double calculateDifficultyLevel(Set<Exercise> exercises) {

        return exercises.stream().mapToDouble(exercise -> exercise.getDifficulty().getDifficultyLevelNumber()).average().orElse(0.0);
    }

    public Workout createWorkout(CreateWorkoutDto createWorkoutDto) throws NotFoundException, IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        String imgName = RandomString.make(45) + "." + this.getFileExtension(createWorkoutDto.getCoverPhoto().getOriginalFilename());

        minioService.createBucket("workout-cover-photos");

        minioService.uploadImageToObjectStorage(
                createWorkoutDto.getCoverPhoto().getInputStream(),
                imgName,
                "workout-cover-photos"
        );

        String imgPath = "workout-cover-photos/" + imgName;

        Set<Exercise> exercisesToAdd = createWorkoutDto
                .getExerciseIds()
                .stream()
                .map(id -> {
                    try {
                        return exerciseService.findById(id);
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toSet());

        return workoutService.saveWorkout(
                Workout
                        .builder()
                        .name(createWorkoutDto.getName())
                        .description(createWorkoutDto.getDescription())
                        .adder(authorityService.getUser())
                        .exercises(exercisesToAdd)
                        .coverPhotoUrl(imgPath)
                        .isGlobal(false)
                        .isDeleted(false)
                        .difficultyLevel(calculateDifficultyLevel(exercisesToAdd))
                        .build()
        );

    }

    private Double calculateDifficultyForProgram(Set<WorkoutProgram> workoutPrograms) {
        return workoutPrograms
                .stream()
                .mapToDouble(
                        workoutProgram ->
                                workoutProgram
                                        .getWorkout()
                                        .getDifficultyLevel()).sum()
                / workoutPrograms.size();
    }

    public Program createProgram(CreateProgramDto createProgramDto) throws NotFoundException {

        Program program = Program
                .builder()
                .name(createProgramDto.getName())
                .description(createProgramDto.getDescription())
                .durationInDays(createProgramDto.getDurationInDays())
                .adder(authorityService.getUser())
                .build();

        program.setWorkoutPrograms(

                createProgramDto
                        .getIndexedWorkouts()
                        .entrySet()
                        .stream()
                        .filter(indexedWorkouts -> indexedWorkouts.getValue() != null)
                        .map(
                                indexedWorkout ->
                                {
                                    try {
                                        return WorkoutProgram
                                                .builder()
                                                .program(program)
                                                .workout(workoutService.findById(indexedWorkout.getValue()))
                                                .workoutIndex(indexedWorkout.getKey())
                                                .build();

                                    } catch (NotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        ).collect(Collectors.toSet())
        );

        program.setDifficultyLevel(this.calculateDifficultyForProgram(program.getWorkoutPrograms()));

        programService.saveProgram(program);
        return program;
    }

    public Set<WorkoutDto> getFilteredWorkouts(WorkoutFilteredRequestDto workoutFilteredRequestDto) throws NotFoundException {


        Set<Workout> workouts = workoutService.getFilteredWorkouts(workoutFilteredRequestDto);

        if (workoutFilteredRequestDto.getIsWorkoutPrivate().equals(true)) {
            workouts
                    .stream()
                    .filter(workout -> !workout.isGlobal())
                    .forEach(
                            workout -> {
                                try {
                                    workout.setCoverPhotoUrl(
                                            minioService.generatePreSignedUrl(
                                                    workout.getCoverPhotoUrl()
                                            )
                                    );
                                } catch (ServerException | InsufficientDataException | ErrorResponseException |
                                         IOException | NoSuchAlgorithmException | InvalidKeyException |
                                         InvalidResponseException | XmlParserException | InternalException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
        }

        return workouts
                .stream()
                .map(workout ->

                        WorkoutDto
                                .builder()
                                .id(workout.getId())
                                .coverPhotoUrl(workout.getCoverPhotoUrl())
                                .difficultyLevel(workout.getDifficultyLevel())
                                .name(workout.getName())
                                .build()

                ).collect(Collectors.toSet());
    }


    @Transactional
    public String uploadProgramCoverPhoto(Long programId, MultipartFile coverPhoto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, NotFoundException {

        String imgName = RandomString.make(45) + "." + this.getFileExtension(coverPhoto.getOriginalFilename());

        minioService.createBucket("program-cover-photos");

        minioService.uploadImageToObjectStorage(
                coverPhoto.getInputStream(),
                imgName,
                "program-cover-photos"
        );

        String imgPath = "program-cover-photos/" + imgName;

        Program programToEdit = programService.getProgramById(programId);

        programToEdit.setCoverPhotoUrl(imgPath);

        return imgPath;
    }

    public Set<SubscribersDto> getSubscribers() throws NotFoundException {
        return
                userService
                        .findById(authorityService.getUserId())
                        .getFollowers()
                        .stream()
                        .map(
                                follower ->
                                        SubscribersDto
                                                .builder()
                                                .id(follower.getId())
                                                .email(follower.getEmail())
                                                .firstName(follower.getFirstname())
                                                .lastName(follower.getLastname())
                                                .role(follower.getRole())
                                                .build()
                        ).collect(Collectors.toSet());
    }

    public List<ExerciseResponseDto> getAllExercisesForCoach() throws NotFoundException {

        return exerciseService
                .getAllExercisesByAdderId
                        (authorityService.getUserId())
                .stream()
                .map(
                        exercise ->
                                ExerciseResponseDto
                                        .builder()
                                        .exerciseId(exercise.getId())
                                        .name(exercise.getName())
                                        .exerciseImageStartUrl(exercise.getExerciseImageStartUrl())
                                        .exerciseImageEndUrl(exercise.getExerciseImageEndUrl())
                                        .muscleGroup(exercise.getMuscleGroup())
                                        .equipment(exercise.getEquipment())
                                        .difficulty(exercise.getDifficulty())
                                        .category(exercise.getCategory())
                                        .isExerciseExclusive(exercise.isExerciseExclusive())
                                        .build()
                ).toList();
    }

    public List<WorkoutDto> getAllWorkoutsForCoach() throws NotFoundException {

        return workoutService
                .getAllWorkoutsByAdderId(
                        authorityService.getUserId()
                ).stream()
                .map(workout ->
                        WorkoutDto
                                .builder()
                                .id(workout.getId())
                                .coverPhotoUrl(workout.getCoverPhotoUrl())
                                .difficultyLevel(workout.getDifficultyLevel())
                                .name(workout.getName())
                                .build())
                .toList();
    }

    public List<ProgramDto> getAllProgramsForCoach() throws NotFoundException {

        return programService
                .getAllByAdderId(
                        authorityService.getUserId()
                )
                .stream()
                .map(
                        program ->
                                ProgramDto
                                        .builder()
                                        .id(program.getId())
                                        .coverPhotoUrl(program.getCoverPhotoUrl())
                                        .difficultyLevel(program.getDifficultyLevel())
                                        .name(program.getName())
                                        .build()
                ).toList();
    }

    public List<CoachDetailsResponseDto> getAllCertificationsForCoach() throws NotFoundException {

        return authorityService
                .getUser()
                .getCoachDetails()
                .stream()
                .map(
                        coachDetails -> {

                            try {
                                return CoachDetailsResponseDto
                                        .builder()
                                        .id(coachDetails.getId())
                                        .imageResourcePreSignedUrl(minioService.generatePreSignedUrl(coachDetails.getCoachCertificatePath()))
                                        .certificationType(coachDetails.getCertificationType())
                                        .isValidated(coachDetails.getIsValidated())
                                        .build();
                            } catch (ServerException | InsufficientDataException | ErrorResponseException |
                                     IOException |
                                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                                     XmlParserException |
                                     InternalException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).toList();
    }

    public DetailedWorkoutDto getDetailedWorkout(Long workoutId) throws NotFoundException {
        Workout workout = workoutService.findById(workoutId);

        try {
            return
                    DetailedWorkoutDto
                            .builder()
                            .id(workoutId)
                            .name(workout.getName())
                            .description(workout.getDescription())
                            .difficultyLevel(workout.getDifficultyLevel())
                            .coverPhotoUrl(minioService.generatePreSignedUrl(workout.getCoverPhotoUrl()))
                            .exercises(workout
                                    .getExercises()
                                    .stream()
                                    .map(
                                            exercise ->
                                            {
                                                try {
                                                    return ExerciseResponseDto
                                                            .builder()
                                                            .exerciseId(exercise.getId())
                                                            .name(exercise.getName())
                                                            .exerciseImageStartUrl(minioService.generatePreSignedUrl(exercise.getExerciseImageStartUrl()))
                                                            .exerciseImageEndUrl(minioService.generatePreSignedUrl(exercise.getExerciseImageEndUrl()))
                                                            .muscleGroup(exercise.getMuscleGroup())
                                                            .equipment(exercise.getEquipment())
                                                            .difficulty(exercise.getDifficulty())
                                                            .category(exercise.getCategory())
                                                            .isExerciseExclusive(exercise.isExerciseExclusive())
                                                            .build();
                                                } catch (ServerException | InsufficientDataException |
                                                         ErrorResponseException | IOException |
                                                         NoSuchAlgorithmException | InvalidKeyException |
                                                         InvalidResponseException | XmlParserException |
                                                         InternalException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                    ).toList()
                            )
                            .build();
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }


    }

    public DetailedProgramDto getDetailedProgram(Long programId) throws NotFoundException {
        Program program = programService.findById(programId);

        try {
            return DetailedProgramDto
                    .builder()
                    .id(programId)
                    .name(program.getName())
                    .description(program.getDescription())
                    .difficultyLevel(program.getDifficultyLevel())
                    .durationInDays(program.getDurationInDays())
                    .coverPhotoUrl(minioService.generatePreSignedUrl(program.getCoverPhotoUrl()))
                    .workoutPrograms(program.getWorkoutPrograms())
                    .build();
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteExercise(Long exerciseId) throws NotFoundException {
        try {
            Exercise exercise = exerciseService.getExerciseByid(exerciseId);

            if (exercise.getExerciseVideoUrl() != null)
                minioService.deleteObject(exercise.getExerciseVideoUrl());
            if (exercise.getExerciseImageStartUrl() != null)
                minioService.deleteObject(exercise.getExerciseImageStartUrl());

            if (exercise.getExerciseImageEndUrl() != null)
                minioService.deleteObject(exercise.getExerciseImageEndUrl());

            for (Workout workout : workoutService.findByExercisesContaining(exercise)) {
                workout.removeExercise(exercise);
                workoutService.save(workout);
            }

            exerciseService.delete(exercise);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }


    public void deleteWorkout(Long workoutId) throws NotFoundException {


        Workout workout = workoutService.findById(workoutId);

        if(workout.getCoverPhotoUrl()!=null) {
            try {
                minioService.deleteObject(workout.getCoverPhotoUrl());
            } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     InternalException e) {
                throw new RuntimeException(e);
            }
        }

        workoutService.delete(workout);
    }

    public void deleteProgram(Long programId) throws NotFoundException {
        Program program = programService.findById(programId);

        if(program.getCoverPhotoUrl()!=null) {
            try {
                minioService.deleteObject(program.getCoverPhotoUrl());
            } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     InternalException e) {
                throw new RuntimeException(e);
            }
        }

        programService.delete(program);

    }

}




