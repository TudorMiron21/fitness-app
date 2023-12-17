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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getEmail() + " unauthorised");
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getEmail() + " unauthorised");
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getEmail() + " not found");
        }
    }

    @PutMapping("/addExerciseToWorkout")
    public ResponseEntity<String> addExerciseToWorkout(@RequestParam(name = "exerciseId") Long exerciseId, @RequestParam(name = "workoutId") Long workoutId) {
        try {
            adminService.addExerciseToWorkout(exerciseId, workoutId);
            ResponseEntity.status(HttpStatus.OK).body("exercise " + exerciseId + " added to workout " + workoutId + " successfully");
        } catch (AuthorizationExceptionHandler aeh) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getEmail() + " unauthorised");
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(nfe.getMessage());
        } catch (AdminUpdateLocalWorkoutException auwe) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("admin cannot update local workouts");
        }
        return null;
    }

    @DeleteMapping("/deleteWorkout/{workoutId}")
    public ResponseEntity<String> deleteWorkout(@PathVariable(name = "workoutId") Long workoutId) {
        try {
            adminService.deleteWorkout(workoutId);
            ResponseEntity.status(HttpStatus.OK).body("workout " + workoutId + " deleted successfully");
        } catch (NotFoundException nfe) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("workout " + workoutId + " not found in the database");
        } catch (AuthorizationExceptionHandler aeh) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getEmail() + " unauthorised");
        } catch (AdminUpdateLocalWorkoutException auwe) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("admin cannot delete local workouts");
        }
        return null;
    }

    @DeleteMapping("deleteExerciseFromWorkout/{exerciseId}/{workoutId}")
    public ResponseEntity<String> deleteExerciseFromWorkout(@PathVariable(name = "exerciseId") Long exerciseId, @PathVariable(name = "workoutId") Long workoutId) {
        try {
            adminService.deleteExerciseFromWorkout(exerciseId, workoutId);
            ResponseEntity.status(HttpStatus.OK).body("exercise " + exerciseId + " from workout " + workoutId + " deleted successfully");
        } catch (NotFoundException nfe) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("workout/exercise not found");
        } catch (AuthorizationExceptionHandler aeh) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getEmail() + " unauthorised");
        } catch (AdminUpdateLocalWorkoutException auwe) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("admin cannot delete exercises from local workouts");
        }
        return null;

    }


}
