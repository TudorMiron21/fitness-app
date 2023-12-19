package tudor.work.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tudor.work.dto.*;
import tudor.work.exceptions.AuthorizationExceptionHandler;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.exceptions.UserAccessException;
import tudor.work.service.AuthorityService;
import tudor.work.service.UserService;

import javax.ws.rs.Path;
import java.util.List;

@RestController
@RequestMapping(path = "/api/selfCoach/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthorityService authorityService;

    //this controller gets all the exercises available from the database depending on the authorities of the user
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
            userService.addExerciseToWorkout(exerciseName,workoutName);
            return ResponseEntity.status(HttpStatus.OK).body("exercise "+exerciseName+ " added successfully to the workout " + workoutName);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthorizationExceptionHandler e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    @PutMapping("/likeWorkout/{workoutId}")
    public ResponseEntity<?> likeWorkout(@PathVariable(name = "workoutId") Long workoutId)
    {
        try {
            userService.likeWorkout(workoutId);
            return ResponseEntity.status(HttpStatus.OK).body("workout " + workoutId + " liked by user " + authorityService.getEmail());

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/unlikeWorkout/{workoutId}")
    public ResponseEntity<?> unlikeWorkout(@PathVariable(name = "workoutId") Long workoutId)
    {
        try {
            userService.unlikeWorkout(workoutId);
            return ResponseEntity.status(HttpStatus.OK).body("workout " + workoutId + " unliked by user " + authorityService.getEmail());

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/workouts/getFirstSixMostLikedWorkouts")
    public ResponseEntity<?> getFirstSixMostLikedWorkouts()
    {
        List<WorkoutDto> workoutDtoList =  userService.getFirstSixMostLikedWorkouts();

        return ResponseEntity.status(HttpStatus.OK).body(workoutDtoList);
    }

    @GetMapping("/workouts/getTopWorkoutsForDifficultyLevel/{lowerLimit}/{upperLimit}")
    public ResponseEntity<?> getTopWorkoutsForDifficultyLevel(@PathVariable(name = "lowerLimit")Double lowerLimit, @PathVariable(name = "upperLimit")Double upperLimit)
    {
        List<WorkoutDto> workoutDtoList =  userService.getTopWorkoutsForDifficultyLevel(lowerLimit,upperLimit);
        return ResponseEntity.status(HttpStatus.OK).body(workoutDtoList);

    }


    @PostMapping("/startWorkout/{workoutId}")
    public ResponseEntity<?> startWorkout(@PathVariable(name = "workoutId") Long workoutId){

        try {
            Long userHistoryWorkoutId = userService.startWorkout(workoutId);
            return ResponseEntity.status(HttpStatus.OK).body(userHistoryWorkoutId);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/saveModule")
    public ResponseEntity<?> saveModule(@RequestBody RequestSaveModuleDto requestSaveModuleDto)
    {
        try {
           Long id = userService.saveModule(requestSaveModuleDto);
            return ResponseEntity.status(HttpStatus.OK).body(id);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/addExerciseToModule/{userHistoryModuleId}")
    public ResponseEntity<?> addExerciseToModule(@PathVariable("userHistoryModuleId") Long userHistoryModuleId, @RequestBody RequestUserHistoryExercise requestUserHistoryExercise)
    {
        try {
            userService.addExerciseToModule(userHistoryModuleId,requestUserHistoryExercise);
            return ResponseEntity.status(HttpStatus.OK).body("exercise has been successfully added to the module");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        }
    }


//    @GetMapping("/getExerciseDetails/{exerciseName}")
//    public ResponseEntity<?> getExerciseDetails(@PathVariable("exerciseName") String exerciseName)
//    {
//
//
//
//    }


    @PostMapping("/addWorkout")
    public ResponseEntity<?> addWorkout(@RequestBody PostWorkoutRequestDto postWorkoutRequestDto)
    {
        try {
            userService.addWorkout(postWorkoutRequestDto);
            return ResponseEntity.status(HttpStatus.OK).body("workout added successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    @PutMapping("/finishWorkout/{userHistoryWorkoutId}")
    public ResponseEntity<?> finishWorkout(@PathVariable(name = "userHistoryWorkoutId")Long userHistoryWorkoutId)
    {
        try {
            userService.finishWorkout(userHistoryWorkoutId);
            return ResponseEntity.status(HttpStatus.OK).body("workout finished");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }


//    @GetMapping("/getStartedWorkouts/{idUser}")
//    public ResponseEntity<?> getStartedWorkouts(@PathVariable("idUser") Long idUser)
//    {
//        userService.getStartedWorkouts(idUser);
//    }


}

