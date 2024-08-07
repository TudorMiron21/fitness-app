package tudor.work.controller;

import com.ctc.wstx.io.ReaderSource;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tudor.work.dto.*;
import tudor.work.exceptions.*;
import tudor.work.model.Gender;
import tudor.work.service.*;
import tudor.work.utils.Range;

import javax.ws.rs.Path;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.HttpHeaders.*;

@RestController
@RequestMapping(path = "/api/selfCoach/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthorityService authorityService;
    private final VideoStreamingService videoService;

    private final PayingUserService payingUserService;


    @Value("${app.streaming.default-chunk-size}")
    public Integer defaultChunkSize;

    @GetMapping("/exercises")
    public List<ExerciseDto> getAllExercises() {
        return userService.getAllExercises();
    }

    @GetMapping("/exercises/exerciseName")
    public ResponseEntity<?> getExerciseByName(@RequestParam(name = "name") String name) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getExerciseByName(name));
        } catch (NotFoundException nfe) {
            // Return a 404 Not Found response with a custom message
            String errorMessage = "Exercise with name " + name + " not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        } catch (RuntimeException re) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getEmail() + " unauthorised");

        }

    }


    @GetMapping("/workouts")
    public ResponseEntity<?> getAllWorkouts() {
        List<WorkoutDto> workouts = userService.getAllWorkouts();
        return ResponseEntity.status(HttpStatus.OK).body(workouts);
    }

    @GetMapping("/workouts/workoutName")
    public ResponseEntity<?> getWorkoutByName(@RequestParam(name = "name") String name) {
        try {
            WorkoutDto workout = userService.getWorkoutByName(name);
            return ResponseEntity.status(HttpStatus.OK).body(workout);
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(nfe.getMessage());
        } catch (UserAccessException uae) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(uae.getMessage());
        }
    }

    @PostMapping("/workout")
    public ResponseEntity<?> addWorkout(WorkoutDto workout) {

        try {
            userService.addWorkout(workout);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user " + authorityService.getEmail() + " not found");

        } catch (DuplicatesException de) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(de.getMessage());
        }

        return null;
    }


    @PutMapping("/workout/{exerciseName}/{workoutName}")
    public ResponseEntity<?> addExerciseToWorkout(@PathVariable(name = "exerciseName") String exerciseName, @PathVariable(name = "workoutName") String workoutName) {

        try {
            userService.addExerciseToWorkout(exerciseName, workoutName);
            return ResponseEntity.status(HttpStatus.OK).body("exercise " + exerciseName + " added successfully to the workout " + workoutName);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthorizationExceptionHandler e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    @PutMapping("/likeWorkout/{workoutId}")
    public ResponseEntity<?> likeWorkout(@PathVariable(name = "workoutId") Long workoutId) {
        try {
            userService.likeWorkout(workoutId);
            return ResponseEntity.status(HttpStatus.OK).body("workout " + workoutId + " liked by user " + authorityService.getEmail());

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/unlikeWorkout/{workoutId}")
    public ResponseEntity<?> unlikeWorkout(@PathVariable(name = "workoutId") Long workoutId) {
        try {
            userService.unlikeWorkout(workoutId);
            return ResponseEntity.status(HttpStatus.OK).body("workout " + workoutId + " unliked by user " + authorityService.getEmail());

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/workouts/getFirstSixMostLikedWorkouts")
    public ResponseEntity<?> getFirstSixMostLikedWorkouts() {
//        List<WorkoutDto> workoutDtoList = userService.getFirstSixMostLikedWorkouts();
        List<WorkoutDto> workoutDtoList = null;
        try {
            workoutDtoList = userService.getHomePageWorkouts();
            return ResponseEntity.status(HttpStatus.OK).body(workoutDtoList);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(workoutDtoList);
        }

    }

    @GetMapping("/workouts/getTopWorkoutsForDifficultyLevel/{lowerLimit}/{upperLimit}")
    public ResponseEntity<?> getTopWorkoutsForDifficultyLevel(@PathVariable(name = "lowerLimit") Double lowerLimit, @PathVariable(name = "upperLimit") Double upperLimit) {
        List<WorkoutDto> workoutDtoList = userService.getTopWorkoutsForDifficultyLevel(lowerLimit, upperLimit);
        return ResponseEntity.status(HttpStatus.OK).body(workoutDtoList);

    }


    @PostMapping("/startWorkout/{workoutId}")
    public ResponseEntity<?> startWorkout(@PathVariable(name = "workoutId") Long workoutId) {

        try {
            Long userHistoryWorkoutId = userService.startWorkout(workoutId);
            return ResponseEntity.status(HttpStatus.OK).body(userHistoryWorkoutId);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/saveModule")
    public ResponseEntity<?> saveModule(@RequestBody RequestSaveModuleDto requestSaveModuleDto) {
        try {
            Long id = userService.saveModule(requestSaveModuleDto);
            return ResponseEntity.status(HttpStatus.OK).body(id);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/updateUserHistoryModule/{userHistoryModuleId}")
    public ResponseEntity<?> updateUserHistoryModule(@PathVariable("userHistoryModuleId") Long userHistoryModuleId, @RequestBody DetailsUserHistoryModuleDto updateUserHistoryModuleDto) {

        try {
            userService.updateUserHistoryModule(userHistoryModuleId, updateUserHistoryModuleDto);
            return ResponseEntity.status(HttpStatus.OK).body("user history module with id " + userHistoryModuleId + " updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PutMapping("/addExerciseToModule/{userHistoryModuleId}")
    public ResponseEntity<?> addExerciseToModule(@PathVariable("userHistoryModuleId") Long userHistoryModuleId, @RequestBody RequestUserHistoryExercise requestUserHistoryExercise) {
        try {
            userService.addExerciseToModule(userHistoryModuleId, requestUserHistoryExercise);
            return ResponseEntity.status(HttpStatus.OK).body("exercise has been successfully added to the module");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        }
    }

    @PutMapping("/updateUserHistoryExercise/{userHistoryExerciseId}")
    public ResponseEntity<?> updateUserHistoryExercise(
            @PathVariable("userHistoryExerciseId") Long userHistoryExerciseId,
            @RequestBody DetailsUserHistoryExerciseDto requestUpdateUserHistoryExerciseDto
    ) {
        try {
            userService.updateExerciseToWorkout(userHistoryExerciseId, requestUpdateUserHistoryExerciseDto);
            return ResponseEntity.status(HttpStatus.OK).body("user history exercise with id " + userHistoryExerciseId + " updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/addWorkout")
    public ResponseEntity<?> addWorkout(@RequestBody PostWorkoutRequestDto postWorkoutRequestDto) {
        try {
            userService.addWorkout(postWorkoutRequestDto);
            return ResponseEntity.status(HttpStatus.OK).body("workout added successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PutMapping("/finishWorkout/{userHistoryWorkoutId}")
    public ResponseEntity<?> finishWorkout(@PathVariable(name = "userHistoryWorkoutId") Long userHistoryWorkoutId) {
        try {
            WorkoutRewardsResponseDto workoutRewardsResponseDto = userService.finishWorkout(userHistoryWorkoutId);
            return ResponseEntity.status(HttpStatus.OK).body(workoutRewardsResponseDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/getStartedWorkouts/{emailUser}")
    public ResponseEntity<?> getStartedWorkouts(@PathVariable("emailUser") String emailUser) {
        try {
            List<WorkoutDto> workouts = userService.getStartedWorkouts(emailUser);
            return ResponseEntity.status(HttpStatus.OK).body(workouts);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/isWorkoutPresentInUserHistory/{workoutId}/{emailUser}")
    public ResponseEntity<?> isWorkoutPresentInUserHistory(@PathVariable("workoutId") Long workoutId, @PathVariable("emailUser") String emailUser) {

        try {
            ResponseWorkoutPresentInUserHistoryDto responseWorkoutPresentInUserHistoryDto = userService.isWorkoutPresentInUserHistory(workoutId, emailUser);
            return ResponseEntity.status(HttpStatus.OK).body(responseWorkoutPresentInUserHistoryDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(e.getMessage());
        }
    }

    @GetMapping("/getLastEntryUserExerciseHistory/{workoutId}/{userEmail}")
    public ResponseEntity<?> getLastEntryUserExerciseHistory(@PathVariable("workoutId") Long workoutId, @PathVariable("userEmail") String userEmail) {
        try {
            ResponseLastEntryUserHistoryExerciseDto responseLastEntryUserHistoryExerciseDto = userService.getLastEntryUserExerciseHistory(workoutId, userEmail);
            return ResponseEntity.status(HttpStatus.OK).body(responseLastEntryUserHistoryExerciseDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getUserHistoryExerciseDetails/{userHistoryExerciseId}")
    public ResponseEntity<?> getUserHistoryExerciseDetails(@PathVariable("userHistoryExerciseId") Long userHistoryExerciseId) {
        try {
            DetailsUserHistoryExerciseDto detailsUserHistoryExerciseDto = userService.getUserHistoryExerciseDetails(userHistoryExerciseId);
            return ResponseEntity.status(HttpStatus.OK).body(detailsUserHistoryExerciseDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getUserHistoryModuleDetails/{userHistoryModuleId}")
    public ResponseEntity<?> getUserHistoryModuleDetails(@PathVariable("userHistoryModuleId") Long userHistoryModuleId) {
        try {
            DetailsUserHistoryModuleDto detailsUserHistoryModuleDto = userService.getUserHistoryModuleDetails(userHistoryModuleId);
            return ResponseEntity.status(HttpStatus.OK).body(detailsUserHistoryModuleDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getAllNonExclusiveExercisesByName/{exerciseName}")
    public ResponseEntity<?> getAllNonExclusiveExercisesByName(@PathVariable("exerciseName") String exerciseName) {
        Set<SimplifiedExerciseDto> simplifiedExerciseDtos = userService.getAllNonExclusiveExercisesByName(exerciseName);
        return ResponseEntity.status(HttpStatus.OK).body(simplifiedExerciseDtos);
    }


    @GetMapping("/getAllNonExclusiveExercises")
    public ResponseEntity<?> getAllNonExclusiveExercises() {
        Set<SimplifiedExerciseDto> simplifiedExerciseDtos = userService.getAllNonExclusiveExercises();
        return ResponseEntity.status(HttpStatus.OK).body(simplifiedExerciseDtos);
    }

    @PostMapping("/createUserWorkout")
    public ResponseEntity<?> createUserWorkout(@RequestBody CreateUserWorkoutDto createUserWorkoutDto) {
        try {
            userService.createUserWorkout(createUserWorkoutDto);
            return ResponseEntity.status(HttpStatus.OK).body("workout added successfully");
        } catch (NotFoundException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/getPersonalWorkouts")
    public ResponseEntity<?> getPersonalWorkouts() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getPersonalWorkouts());
    }

    @GetMapping("/getLastWorkoutStatistics")
    public ResponseEntity<?> getLastWorkoutStatistics() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getLastWorkoutStatistics());
    }

    @GetMapping("/getGeneralWorkoutInformation/{noWorkoutResults}")
    public ResponseEntity<?> getGeneralWorkoutInformation(@PathVariable("noWorkoutResults") Integer noWorkoutResults) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getGeneralWorkoutInformation(noWorkoutResults));
    }

    @GetMapping("/videoStreaming/{exerciseId}")
    public ResponseEntity<?> readChunk(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range,
            @PathVariable Long exerciseId
    ) {
        try {
            Range parsedRange = Range.parseHttpRangeString(range, defaultChunkSize);
            VideoStreamingService.ChunkWithMetadata chunkWithMetadata = videoService.fetchChunk(exerciseId, parsedRange);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .header(CONTENT_TYPE, "video/mp4")
                    .header(ACCEPT_RANGES, "bytes")
                    .header(CONTENT_LENGTH, calculateContentLengthHeader(parsedRange, chunkWithMetadata.fileSize()))
                    .header(CONTENT_RANGE, constructContentRangeHeader(parsedRange, chunkWithMetadata.fileSize()))
                    .body(chunkWithMetadata.chunk());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    private String calculateContentLengthHeader(Range range, long fileSize) {
        return String.valueOf(range.getRangeEnd(fileSize) - range.getRangeStart() + 1);
    }

    private String constructContentRangeHeader(Range range, long fileSize) {
        return "bytes " + range.getRangeStart() + "-" + range.getRangeEnd(fileSize) + "/" + fileSize;
    }


    @GetMapping("/getPersonalRecordsForUser")
    public ResponseEntity<?> getPersonalRecordsForUser() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getPersonalRecordsForUser());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping("/getLeaderBoardEntries")
    public ResponseEntity<?> getLeaderBoardEntries() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getLeaderBoardEntries());
    }


    @GetMapping("/getContactsLeaderBoard")
    public ResponseEntity<?> getContactsLeaderBoard(@RequestParam("email") List<String> emails) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getContactsLeaderBoard(emails));
    }

    @GetMapping("/getFilteredWorkouts")
    public ResponseEntity<?> getFilteredWorkouts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minDifficultyLevel") Double minDifficultyLevel,
            @RequestParam(value = "maxDifficultyLevel") Double maxDifficultyLevel) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getFilteredWorkouts(FilterSearchDto
                    .builder()
                    .name(name)
                    .minDifficulty(minDifficultyLevel)
                    .maxDifficulty(maxDifficultyLevel)
                    .build()
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getFilteredPrograms")
    public ResponseEntity<?> getFilteredPrograms(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minDifficultyLevel") Double minDifficultyLevel,
            @RequestParam(value = "maxDifficultyLevel") Double maxDifficultyLevel
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getFilteredPrograms(FilterSearchDto
                    .builder()
                    .name(name)
                    .minDifficulty(minDifficultyLevel)
                    .maxDifficulty(maxDifficultyLevel)
                    .build()
            ));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getUserAchievements")
    public ResponseEntity<?> getUserAchievements() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUserAchievements());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getLeaderBoardEntryForUser")
    public ResponseEntity<?> getLeaderBoardEntryForUser() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getLeaderBoardEntryForUser());
        } catch (LeaderBoardEntryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/webhookResponse")
    public ResponseEntity<?> webhookResponse(@RequestBody Map<String, Object> requestBody)
    {
        return ResponseEntity.status(HttpStatus.OK).body(userService.webhookResponse(requestBody));
    }


    @PutMapping("/uploadUserDetails/{gender}/{age}/{height}/{currentWeight}/{goalWeight}")
    public ResponseEntity<?>uploadUserDetails(
            @PathVariable("gender")Gender gender,
            @PathVariable("age") Integer age,
            @PathVariable("height")Integer height,
            @PathVariable("currentWeight")Double currentWeight,
            @PathVariable("goalWeight")Double goalWeight
            )
    {
        try {
            userService.uploadUserDetails(gender,age,height,currentWeight,goalWeight);
            return ResponseEntity.status(HttpStatus.OK).body("User details saved successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while saving user details");        }
    }


    @GetMapping("/getAllPrograms")
    public ResponseEntity<?> getAllPrograms() {
        Set<ProgramDto> programs = payingUserService.getTopPrograms();
        return ResponseEntity.status(HttpStatus.OK).body(programs);
    }


    @PostMapping("/startProgram/{programId}")
    public ResponseEntity<?> startProgram(@PathVariable(name = "programId") Long programId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(payingUserService.startProgram(programId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @PutMapping("/addWorkoutToProgram/{workoutId}/{userHistoryProgramId}")
    public ResponseEntity<?> addWorkoutToProgram(@PathVariable("workoutId") Long workoutId, @PathVariable("userHistoryProgramId") Long userHistoryProgramId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(payingUserService.addWorkoutToProgram(userHistoryProgramId, workoutId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping("/isProgramStarted/{programId}")
    public ResponseEntity<?> isProgramStarted(@PathVariable(name = "programId") Long programId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(payingUserService.isProgramStarted(programId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        } catch (UserHistoryProgramNotFoundException e) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(e);
        }
    }

    @GetMapping("/getUserDetails")
    public ResponseEntity<?> getUserDetails()
    {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUserDetails());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/uploadProfilePicture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            userService.uploadProfilePicture(file);
            return ResponseEntity.status(HttpStatus.OK).body("Profile picture added successfully");
        } catch (IOException | NotFoundException | ServerException | InsufficientDataException |
                 ErrorResponseException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                 XmlParserException | InternalException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error during picture upload");
        }
    }

    @GetMapping("/getProfilePicture")
    public ResponseEntity<?> getProfilePicture(){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getProfilePicture());
        } catch (NotFoundException | ServerException | InsufficientDataException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException | ErrorResponseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}

