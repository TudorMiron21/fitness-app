package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.ProgramDto;
import tudor.work.dto.RequestSaveWorkoutToProgramDto;
import tudor.work.dto.UserDto;
import tudor.work.exceptions.DuplicateCoachSubscription;

import tudor.work.exceptions.UserHistoryProgramNotFoundException;
import tudor.work.model.Program;
import tudor.work.model.User;
import tudor.work.model.UserHistoryProgram;
import tudor.work.model.UserHistoryWorkout;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayingUserService {

    private final AuthorityService authorityService;
    private final UserService userService;
    private final UserHistoryProgramService userHistoryProgramService;
    private final ProgramService programService;
    private final UserHistoryWorkoutService userHistoryWorkoutService;

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

    public UserHistoryProgram startProgram(Long programId) throws NotFoundException {
        Program program = programService.findById(programId);


        UserHistoryProgram userHistoryProgram =
                UserHistoryProgram
                        .builder()
                        .program(program)
                        .userHistoryWorkouts(new ArrayList<>())
                        .user(authorityService.getUser())
                        .isProgramDone(false)
                        .currentWorkoutIndex(program.getLowestIndex())
                        .startedWorkoutDateAndTime(LocalDateTime.now())
                        .build();

        UserHistoryProgram saveduserHistoryProgram = userHistoryProgramService.save(userHistoryProgram);

        return saveduserHistoryProgram;
    }


    @Transactional
    public Long addWorkoutToProgram(Long parentUserHistoryProgramId, Long workoutId) throws NotFoundException {
        UserHistoryProgram userHistoryProgram = userHistoryProgramService.findById(parentUserHistoryProgramId);


        Long userHistoryWorkoutId = userService.startWorkout(workoutId);

        UserHistoryWorkout userHistoryWorkout = userHistoryWorkoutService.findById(userHistoryWorkoutId);

        userHistoryWorkout.setUserHistoryProgram(userHistoryProgram);

        return userHistoryWorkout.getId();
    }


    public UserHistoryProgram isProgramStarted(Long programId) throws NotFoundException, UserHistoryProgramNotFoundException {
        Optional<UserHistoryProgram> userHistoryProgram = userHistoryProgramService.findLastProgramByUserAndProgramId(authorityService.getUserId(), programId);

        if (userHistoryProgram.isPresent()) {
            if(!userHistoryProgram.get().getIsProgramDone()) {
                return userHistoryProgram.get();
            }
            else {
                throw new UserHistoryProgramNotFoundException("program with id " + programId + " found in program user history, but its done");
            }
        } else {
            throw new UserHistoryProgramNotFoundException("program with id " + programId + " not found in program user history");
        }
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
