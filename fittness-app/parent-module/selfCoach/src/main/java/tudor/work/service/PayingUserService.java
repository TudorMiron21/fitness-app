package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.*;
import tudor.work.exceptions.DuplicateCoachSubscription;

import tudor.work.exceptions.NotSubscribedException;
import tudor.work.exceptions.UserHistoryProgramNotFoundException;
import tudor.work.model.*;

import javax.swing.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.List;

import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayingUserService {

    private final AuthorityService authorityService;
    private final UserService userService;
    private final UserHistoryProgramService userHistoryProgramService;
    private final ProgramService programService;
    private final UserHistoryWorkoutService userHistoryWorkoutService;
    private final ExerciseService exerciseService;
    private final WorkoutService workoutService;

    @Transactional
    public void subscribeToCoach(Long coachId) throws NotFoundException, DuplicateCoachSubscription {

        User payingUser = userService.findById(authorityService.getUserId());
        User coach = userService.findById(coachId);

        if (!payingUser.getFollowing().add(coach))
            throw new DuplicateCoachSubscription("paying user" + payingUser.getId() + " already subscribed to coach" + coachId);

        if (!coach.getFollowers().add(payingUser))
            throw new DuplicateCoachSubscription("coach" + coachId + " has paying user" + payingUser.getId() + "as a subscriber");
    }

    @Transactional
    public void unsubscribeFromCoach(Long coachId) throws NotFoundException, DuplicateCoachSubscription {

        User payingUser = userService.findById(authorityService.getUserId());
        User coach = userService.findById(coachId);

        payingUser.getFollowing().remove(coach);
        coach.getFollowers().remove(payingUser);
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
            if (!userHistoryProgram.get().getIsProgramDone()) {
                return userHistoryProgram.get();
            } else {
                throw new UserHistoryProgramNotFoundException("program with id " + programId + " found in program user history, but its done");
            }
        } else {
            throw new UserHistoryProgramNotFoundException("program with id " + programId + " not found in program user history");
        }
    }


    public Set<ProgramDto> getTopPrograms() {

//        return programService.findAllPrograms()
//                .stream()
//                .map(
//                        program ->
//                                ProgramDto
//                                        .builder()
//                                        .id(program.getId())
//                                        .name(program.getName())
//                                        .description(program.getDescription())
//                                        .durationInDays(program.getDurationInDays())
//                                        .coverPhotoUrl(program.getCoverPhotoUrl())
//                                        .workoutProgramSet(program.getWorkoutPrograms())
//                                        .build()
//                ).collect(Collectors.toSet());

        List<Program> allPrograms = programService.findAllPrograms();

        Predicate<Program> isProgramAddedByAdmin = program -> {
            User programAdder = program.getAdder();
            return programAdder.getRole().equals(Roles.ADMIN);
        };

        Predicate<Program> isProgramAddedByFollowingCoach  = program ->
        {
            try {
                User payingUser = authorityService.getUser();
                if(payingUser.getRole().equals(Roles.USER)) return false;
                return payingUser.getFollowing().contains(program.getAdder());
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        };

        return allPrograms
                .stream()
                .filter(isProgramAddedByAdmin.or(isProgramAddedByFollowingCoach))
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

    public Boolean isCoachFollowedByUser(Long coachId) throws NotFoundException {

        User coach = userService.findById(coachId);

        User payingUser = userService.findById(authorityService.getUserId());

        return payingUser.getFollowing().contains(coach);
    }

    @Transactional
    public void toggleFollowCoach(Long coachId) throws NotFoundException, DuplicateCoachSubscription, NotSubscribedException {

        User payingUser = userService.findById(authorityService.getUserId());
        User coach = userService.findById(coachId);

        if (coach.getFollowers().contains(payingUser) && payingUser.getFollowing().contains(coach)) {
            //the user is subscribed, and now we want to unsubscribe
            if (!payingUser.getFollowing().remove(coach))
                throw new NotSubscribedException("paying user " + payingUser.getId() + " is not subscribed to coach " + coachId);

            if (!coach.getFollowers().remove(payingUser))
                throw new NotSubscribedException("coach " + coachId + " does not have paying user" + payingUser.getId() + "as a subscriber");
        } else if (!coach.getFollowers().contains(payingUser) && !payingUser.getFollowing().contains(coach)) {
            //the user is not subscribed, and now we want to subscribe
            if (!payingUser.getFollowing().add(coach))
                throw new DuplicateCoachSubscription("paying user" + payingUser.getId() + " already subscribed to coach" + coachId);

            if (!coach.getFollowers().add(payingUser))
                throw new DuplicateCoachSubscription("coach" + coachId + " already has paying user" + payingUser.getId() + "as a subscriber");
        } else {
            throw new RuntimeException("coach/payingUser follows/not follows payingUser/coach, but not vice versa");
        }
    }

    public CoachDetailsDto getCoachDetails(Long coachId) throws NotFoundException {
        User coach = userService.findById(coachId);

        return CoachDetailsDto
                .builder()
                .numberOfSubscribers(coach.getFollowers().size())
                .numberOfChallenges(0)
                .numberOfExercises(exerciseService.getNumberOfExercisesForCoach(coach))
                .numberOfWorkouts(workoutService.getNumberOfWorkoutsForCoach(coach))
                .numberOfPrograms(programService.getNumberOfProgramsForCoach(coach))
                .build();

    }

    public Set<WorkoutDto> getCoachWorkouts(Long coachId) throws NotFoundException {

        User coach = userService.findById(coachId);
        List<Workout> coachWorkouts = workoutService.findAllByAdder(coach);

        return coachWorkouts
                .stream()
                .map(
                        workout ->
                        {
                            try {
                                return WorkoutDto
                                        .builder()
                                        .id(workout.getId())
                                        .name(workout.getName())
                                        .description(workout.getDescription())
                                        .coverPhotoUrl(workout.getCoverPhotoUrl())
                                        .difficultyLevel(workout.getDifficultyLevel())
                                        .isLikedByUser(workout.getLikers().contains(authorityService.getUser()))
                                        .noLikes(((long) workout.getLikers().size()))
                                        .exercises(workout.getExercises())
                                        .build();
                            } catch (NotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }

                ).collect(Collectors.toSet());
    }

    public Set<ProgramDto> getCoachPrograms(Long coachId) throws NotFoundException {
        User coach = userService.findById(coachId);

        List<Program> programs = programService.findAllByAdder(coach);

        return programs
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
