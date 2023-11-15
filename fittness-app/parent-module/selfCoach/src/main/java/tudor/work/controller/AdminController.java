package tudor.work.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tudor.work.dto.ExerciseDto;
import tudor.work.dto.WorkoutDto;
import tudor.work.exceptions.AdminUpdateLocalWorkoutException;
import tudor.work.exceptions.AuthorizationExceptionHandler;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.service.AdminService;
import tudor.work.service.AuthorityService;

@RestController
@RequestMapping(path = "/api/selfCoach/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {


    private final AdminService adminService;
    private final AuthorityService authorityService;

    @PostMapping("/addExercise")
    public ResponseEntity<String> addExercise(@RequestBody ExerciseDto exercise) {
        try {
            adminService.addExercise(exercise);
            return ResponseEntity.status(HttpStatus.OK).body("exercise added successfully");
        } catch (DuplicatesException de) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("exercise " + exercise.getName() + " already exists");
        } catch (AuthorizationExceptionHandler aeh) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getUserName() + " unauthorised");
        }
    }

    //this controller is used for adding global users
    @PostMapping("/addWorkout")
    public ResponseEntity<String> addWorkout(@RequestBody WorkoutDto workoutDto) {
        try {
            adminService.addWorkout(workoutDto);
            return ResponseEntity.status(HttpStatus.OK).body("workout added successfully");
        } catch (DuplicatesException de) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("workout " + workoutDto.getName() + " already exists");
        } catch (AuthorizationExceptionHandler aeh) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getUserName() + " unauthorised");
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getUserName() + " not found");
        }
    }

    @PutMapping("/addExerciseToWorkout")
    public ResponseEntity<String> addExerciseToWorkout(@RequestParam(name = "exerciseName") String exerciseName, @RequestParam(name = "workoutName") String workoutName) {
        try {
            adminService.addExerciseToWorkout(exerciseName, workoutName);
            ResponseEntity.status(HttpStatus.OK).body("exercise " + exerciseName + " added to workout " + workoutName + " successfully");
        } catch (AuthorizationExceptionHandler aeh) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getUserName() + " unauthorised");
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(nfe.getMessage());
        } catch (AdminUpdateLocalWorkoutException auwe) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("admin cannot update local workouts");
        }
        return null;
    }

    @DeleteMapping("/deleteWorkout/{workoutName}")
    public ResponseEntity<String> deleteWorkout(@PathVariable(name = "workoutName") String workoutName) {
        try {
            adminService.deleteWorkout(workoutName);
            ResponseEntity.status(HttpStatus.OK).body("workout " + workoutName + " deleted successfully");
        } catch (NotFoundException nfe) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("workout " + workoutName + " not found in the database");
        } catch (AuthorizationExceptionHandler aeh) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getUserName() + " unauthorised");
        } catch (AdminUpdateLocalWorkoutException auwe) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("admin cannot delete local workouts");
        }
        return null;
    }

    @DeleteMapping("deleteExerciseFromWorkout/{exerciseName}/{workoutName}")
    public ResponseEntity<String> deleteExerciseFromWorkout(@PathVariable(name = "exerciseName") String exerciseName, @PathVariable(name = "workoutName") String workoutName) {
        try {
            adminService.deleteExerciseFromWorkout(exerciseName, workoutName);
            ResponseEntity.status(HttpStatus.OK).body("exercise " + exerciseName + " from workout " + workoutName + " deleted successfully");
        } catch (NotFoundException nfe) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("workout/exercise not found");
        } catch (AuthorizationExceptionHandler aeh) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getUserName() + " unauthorised");
        } catch (AdminUpdateLocalWorkoutException auwe) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("admin cannot delete exercises from local workouts");
        }
        return null;

    }


}
