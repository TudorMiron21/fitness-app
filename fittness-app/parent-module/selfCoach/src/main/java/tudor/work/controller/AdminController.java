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
import tudor.work.exceptions.AuthenticationExceptionHandler;
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

    @GetMapping("/get")
    public String get() {
        return "GET:: admin controller";
    }

//    @PostMapping
//    @PreAuthorize("hasAuthority(admin:write)")
//    public String post() {
//        return "POST:: admin controller";
//    }

    @PutMapping
    @PreAuthorize("hasAuthority(admin:update)")
    public String put() {
        return "PUT:: admin controller";
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(admin:delete)")
    public String delete() {
        return "DELETE:: admin controller";
    }

    @PostMapping("/addExercise")
    public ResponseEntity<String> addExercise(@RequestBody ExerciseDto exercise) {
        try {
            adminService.addExercise(exercise);
            return ResponseEntity.status(HttpStatus.OK).body("exercise added successfully");
        } catch (DuplicatesException de) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("exercise " + exercise.getName() + " already exists");
        } catch (AuthenticationExceptionHandler aeh) {
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
        } catch (AuthenticationExceptionHandler aeh) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getUserName() + " unauthorised");
        } catch (NotFoundException nfe) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getUserName() + " not found");
        }
    }

    @PutMapping("/addExerciseToWorkout")
    public ResponseEntity<String> addExerciseToWorkout(@RequestParam(name = "exerciseName") String exerciseName, @RequestParam(name = "workoutName") String workoutName) {
        try{
            adminService.addExerciseToWorkout(exerciseName,workoutName);
            ResponseEntity.status(HttpStatus.OK).body("exercise "+ exerciseName+ " added to workout "+workoutName +" successfully");
        }
        catch(AuthenticationExceptionHandler aeh)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user " + authorityService.getUserName() + " unauthorised");
        }catch(NotFoundException nfe){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("exercise/workout not found in the database");
        }catch(RuntimeException re)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("admin cannot update local workouts");
        }
        return null;
    }


}
