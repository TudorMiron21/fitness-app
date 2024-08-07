package tudor.work.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.simpleframework.xml.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tudor.work.dto.ProgramDto;
import tudor.work.dto.UserDto;
import tudor.work.exceptions.DuplicateCoachSubscription;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.exceptions.NotSubscribedException;
import tudor.work.exceptions.UserHistoryProgramNotFoundException;
import tudor.work.service.PayingUserService;
import tudor.work.service.UserService;

import java.util.Set;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/selfCoach/payingUser")
@PreAuthorize("not hasRole('USER')")
public class PayingUserController {

    private final PayingUserService payingUserService;
    private final UserService userService;

    @PutMapping("/subscribeToCoach/{coachId}")
    public ResponseEntity<?> subscribeToCoach(@PathVariable(name = "coachId") Long coachId) {

        try {
            payingUserService.subscribeToCoach(coachId);
            return ResponseEntity.status(HttpStatus.OK).body("subscribed");
        } catch (NotFoundException | DuplicateCoachSubscription e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/getFollowingCoaches")
    public ResponseEntity<?> getFollowingCoaches() {
        try {
            Set<UserDto> followingCoaches = payingUserService.getFollowingCoaches();
            return ResponseEntity.status(HttpStatus.OK).body(followingCoaches);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);

        }
    }

    @GetMapping("/getNonFollowingCoaches")
    public ResponseEntity<?> getNonFollowingCoaches() {
        try {
            Set<UserDto> followingCoaches = payingUserService.getNonFollowingCoaches();
            return ResponseEntity.status(HttpStatus.OK).body(followingCoaches);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }


//    @GetMapping("/getAllPrograms")
//    public ResponseEntity<?> getAllPrograms() {
//        Set<ProgramDto> programs = payingUserService.getTopPrograms();
//        return ResponseEntity.status(HttpStatus.OK).body(programs);
//    }
//
//
//    @PostMapping("/startProgram/{programId}")
//    public ResponseEntity<?> startProgram(@PathVariable(name = "programId") Long programId) {
//        try {
//            return ResponseEntity.status(HttpStatus.OK).body(payingUserService.startProgram(programId));
//        } catch (NotFoundException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
//        }
//    }
//
//    @PutMapping("/addWorkoutToProgram/{workoutId}/{userHistoryProgramId}")
//    public ResponseEntity<?> addWorkoutToProgram(@PathVariable("workoutId") Long workoutId, @PathVariable("userHistoryProgramId") Long userHistoryProgramId) {
//        try {
//            return ResponseEntity.status(HttpStatus.OK).body(payingUserService.addWorkoutToProgram(userHistoryProgramId, workoutId));
//        } catch (NotFoundException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
//        }
//    }
//
//    @GetMapping("/isProgramStarted/{programId}")
//    public ResponseEntity<?> isProgramStarted(@PathVariable(name = "programId") Long programId) {
//        try {
//            return ResponseEntity.status(HttpStatus.OK).body(payingUserService.isProgramStarted(programId));
//        } catch (NotFoundException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
//        } catch (UserHistoryProgramNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.ACCEPTED).body(e);
//        }
//    }

    @GetMapping("/isCoachFollowedByUser/{coachId}")
    public ResponseEntity<?> isCoachFollowedByUser(@PathVariable("coachId") Long coachId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(payingUserService.isCoachFollowedByUser(coachId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @PutMapping("/toggleFollowCoach/{coachId}")
    public ResponseEntity<?> toggleFollowCoach(@PathVariable("coachId") Long coachId) {
        try {
            payingUserService.toggleFollowCoach(coachId);
            return ResponseEntity.status(HttpStatus.OK).body("subscribed/unsubscribed successfully");
        } catch (NotFoundException | DuplicateCoachSubscription | NotSubscribedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping("/getCoachDetails/{coachId}")
    public ResponseEntity<?> getCoachDetails(@PathVariable("coachId") Long coachId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(payingUserService.getCoachDetails(coachId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping("/getCoachWorkouts/{coachId}")
    public ResponseEntity<?> getCoachWorkouts(@PathVariable("coachId") Long coachId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(payingUserService.getCoachWorkouts(coachId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping("/getCoachPrograms/{coachId}")
    public ResponseEntity<?> getCoachPrograms(@PathVariable("coachId")Long coachId)
    {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(payingUserService.getCoachPrograms(coachId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }


}

