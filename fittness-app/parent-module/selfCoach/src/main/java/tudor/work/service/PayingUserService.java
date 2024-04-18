package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.ProgramDto;
import tudor.work.dto.UserDto;
import tudor.work.exceptions.DuplicateCoachSubscription;

import tudor.work.model.Program;
import tudor.work.model.User;
import tudor.work.model.UserHistoryProgram;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayingUserService {

    private final AuthorityService authorityService;
    private final UserService userService;
    private final UserHistoryProgramService userHistoryProgramService;
    private final ProgramService programService;

    @Transactional
    public void subscribeToCoach(Long coachId) throws NotFoundException, DuplicateCoachSubscription {

        User payingUser = userService.findById(authorityService.getUserId());
        User coach = userService.findById(coachId);

        if (!payingUser.getFollowing().add(coach))
            throw new DuplicateCoachSubscription("paying user" + payingUser.getId() + " already subscribed to coach" + coachId);

        if (!coach.getFollowers().add(payingUser))
            throw new DuplicateCoachSubscription("coach" + coachId + " has paying user" + payingUser.getId() + "as a subscriber");


    }

    public Set<UserDto> getFollowingCoaches() throws NotFoundException {

        User payingUser = authorityService.getUser();

        return
                payingUser
                        .getFollowing()
                        .stream()
                        .map(
                                coach ->
                                        UserDto
                                                .builder()
                                                .id(coach.getId())
                                                .email(coach.getEmail())
                                                .firstName(coach.getFirstname())
                                                .lastName(coach.getLastname())
//                                     .profilePictureUrl()
                                                .build()
                        ).collect(Collectors.toSet());


    }

    public Long startProgram(Long programId) throws NotFoundException {
        Program program = programService.findById(programId);


        UserHistoryProgram userHistoryProgram =
                UserHistoryProgram
                        .builder()
                        .program(program)
                        .userHistoryWorkouts(new ArrayList<>())
                        .user(authorityService.getUser())
                        .isProgramDone(false)
                        .startedWorkoutDateAndTime(LocalDateTime.now())
                        .build();


        UserHistoryProgram saveduserHistoryProgram = userHistoryProgramService.save(userHistoryProgram);

        return saveduserHistoryProgram.getId();

    }

    public Set<ProgramDto> getTopPrograms() {

        return programService.findAllPrograms()
                .stream()
                .map(
                        program ->
                                ProgramDto
                                        .builder()
                                        .id(program.getId())
                                        .name(program.getName())
                                        .description(program.getDescription())
                                        .durationInDays(program.getDurationInDays())
                                        .coverPhotoUrl(program.getCoverPhotoUrl())
                                        .workoutProgramSet(program.getWorkoutPrograms())
                                        .build()
                ).collect(Collectors.toSet());
    }


}
