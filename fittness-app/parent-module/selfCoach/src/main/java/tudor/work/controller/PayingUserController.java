package tudor.work.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tudor.work.dto.ProgramDto;
import tudor.work.dto.UserDto;
import tudor.work.exceptions.DuplicateCoachSubscription;
import tudor.work.exceptions.DuplicatesException;
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
    public ResponseEntity<?> getFollowingCoaches()
    {
        try {
            Set<UserDto> followingCoaches =payingUserService.getFollowingCoaches();
            return ResponseEntity.status(HttpStatus.OK).body(followingCoaches);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);

        }
    }

    @GetMapping("/getAllPrograms")
    public ResponseEntity<?> getAllPrograms()
    {
        Set<ProgramDto> programs = payingUserService.getTopPrograms();
        return ResponseEntity.status(HttpStatus.OK).body(programs);
    }

}

