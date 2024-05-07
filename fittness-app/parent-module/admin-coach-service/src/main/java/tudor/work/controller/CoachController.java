package tudor.work.controller;

import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.checkerframework.framework.qual.RequiresQualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.multipart.MultipartFile;
import tudor.work.dto.*;
import tudor.work.model.Category;
import tudor.work.service.*;

import javax.swing.text.StyledEditorKit;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/api/v1/adminCoachService/coach")
@RequiredArgsConstructor
public class CoachController {

    private final CoachService coachService;

    @PostMapping("/certificateCoach")
    public ResponseEntity<?> certificateCoach() {
        return null;
    }

    @PostMapping("/uploadCoachDetails")
    public ResponseEntity<?> uploadCoachDetails(@ModelAttribute UploadCoachDetailsRequestDto uploadCoachDetailsRequestDto) {
        try {
            if (uploadCoachDetailsRequestDto.getCertificateImg().isEmpty()) {
                return ResponseEntity.badRequest().body("no file was uploaded");
            }

            coachService.uploadCoachDetails(uploadCoachDetailsRequestDto);
            return ResponseEntity.status(HttpStatus.OK).body("coach details uploaded");
        } catch (ServerException | InvalidResponseException | InsufficientDataException | ErrorResponseException |
                 IOException | NoSuchAlgorithmException | InvalidKeyException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/checkAreCoachDetailsValid")
    public ResponseEntity<?> checkAreCoachDetailsValid() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(coachService.checkAreCoachDetailsValid());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
        }
    }


    @PostMapping("/uploadExerciseDetails")
    public ResponseEntity<?> uploadExerciseDetails(@ModelAttribute UploadExerciseDetailsDto uploadExerciseDetailsDto) {

        try {
            Map<String, Object> response = coachService.UploadExerciseDetailsAndInitMultipart("exercise-videos", "application/octet-stream", uploadExerciseDetailsDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }

    }

    @PutMapping("/completeMultipartUpload")
    public ResponseEntity<?> completeMultipartUpload(@RequestBody CompleteMultipartUploadDto completeMultipartUploadDto) {
        Boolean uploadCompleted = null;
        try {
            uploadCompleted = coachService.completeMultipartUpload("exercise-videos", completeMultipartUploadDto);
            if (uploadCompleted)
                return ResponseEntity.status(HttpStatus.OK).body(uploadCompleted);
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(uploadCompleted);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping("/getFilteredExercises")
    public ResponseEntity<?> getFilteredExercises(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "isExercisePrivate", required = false) Boolean isExercisePrivate,
            @RequestParam(value = "muscleGroupNames", required = false) List<String> muscleGroupNames,
            @RequestParam(value = "equipmentNames", required = false) List<String> equipmentNames,
            @RequestParam(value = "difficultyNames", required = false) List<String> difficultyNames,
            @RequestParam(value = "categoryNames", required = false) List<String> categoryNames
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(coachService
                            .getFilteredExercises(
                                    ExerciseFilteredRequestDto
                                            .builder()
                                            .name(name)
                                            .isExercisePrivate(isExercisePrivate)
                                            .muscleGroupNames(muscleGroupNames != null ? new HashSet<>(muscleGroupNames) : null)
                                            .equipmentNames(equipmentNames != null ? new HashSet<>(equipmentNames) : null)
                                            .difficultyNames(difficultyNames != null ? new HashSet<>(difficultyNames) : null)
                                            .categoryNames(categoryNames != null ? new HashSet<>(categoryNames) : null)
                                            .build()
                            )
                    );
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping("/getFilteredWorkouts")
    public ResponseEntity<?> getFilteredWorkouts(

            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "isWorkoutPrivate", required = false) Boolean isWorkoutPrivate,
            @RequestParam(value = "minDifficultyLevel") Double minDifficultyLevel,
            @RequestParam(value = "maxDifficultyLevel") Double maxDifficultyLevel
    ) {

        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    coachService.getFilteredWorkouts(
                            WorkoutFilteredRequestDto
                                    .builder()
                                    .name(name)
                                    .isWorkoutPrivate(isWorkoutPrivate)
                                    .minDifficulty(minDifficultyLevel)
                                    .maxDifficulty(maxDifficultyLevel)
                                    .build()
                    )
            );
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }

    }


    @PostMapping("/createWorkout")
    public ResponseEntity<?> createWorkout(@ModelAttribute CreateWorkoutDto createWorkoutDto) {

        try {
            return ResponseEntity.status(HttpStatus.OK).body(coachService.createWorkout(createWorkoutDto));
        } catch (NotFoundException | IOException | ServerException | InsufficientDataException |
                 ErrorResponseException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                 XmlParserException | InternalException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }


    @PostMapping("/createProgram")
    public ResponseEntity<?> createProgram(@RequestBody CreateProgramDto createProgramDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(coachService.createProgram(createProgramDto));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }


    @PutMapping("/uploadProgramCoverPhoto/{programId}")
    public ResponseEntity<?> uploadProgramCoverPhoto(@PathVariable Long programId, MultipartFile coverPhoto) {

        try {
            String imgPath = coachService.uploadProgramCoverPhoto(programId, coverPhoto);

            return ResponseEntity.status(HttpStatus.OK).body(imgPath);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.FOUND).body(e);
        }
    }

    @GetMapping("/getSubscribers")
    public ResponseEntity<?> getSubscribers() {
        try {
            Set<SubscribersDto> subscribers = coachService.getSubscribers();
            return ResponseEntity.status(HttpStatus.OK).body(subscribers);
        } catch (NotFoundException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping("/getAllExercisesForCoach")
    public ResponseEntity<?> getAllExercisesForCoach() {

        try {
            return ResponseEntity.status(HttpStatus.OK).body(coachService.getAllExercisesForCoach());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getAllWorkoutsForCoach")
    public ResponseEntity<?> getAllWorkoutsForCoach() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(coachService.getAllWorkoutsForCoach());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getAllProgramsForCoach")
    public ResponseEntity<?> getAllProgramsForCoach() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(coachService.getAllProgramsForCoach());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getAllCertificationsForCoach")
    public ResponseEntity<?> getAllCertificationsForCoach()
    {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(coachService.getAllCertificationsForCoach());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getDetailedWorkout/{workoutId}")
    public ResponseEntity<?> getDetailedWorkout(@PathVariable(name = "workoutId")Long workoutId)
    {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(coachService.getDetailedWorkout(workoutId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getDetailedProgram/{programId}")
    public ResponseEntity<?> getDetailedProgram(@PathVariable(name = "programId")Long programId)
    {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(coachService.getDetailedProgram(programId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
