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
import tudor.work.exceptions.UserAccessException;
import tudor.work.model.Workout;
import tudor.work.service.AuthorityService;
import tudor.work.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/selfCoach/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthorityService authorityService;

    //    @GetMapping
//    public String get() {
//        return "GET:: user controller";
//    }
    @PostMapping
    public String post() {
        return "POST:: user controller";
    }

    @PutMapping
    public String put() {
        return "PUT:: user controller";
    }

    @DeleteMapping
    public String delete() {
        return "DELETE:: user controller";
    }


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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getUserName() + " unauthorised");

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


}

