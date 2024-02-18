package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.UserHistoryWorkout;
import tudor.work.repository.UserHistoryWorkoutRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserHistoryWorkoutService {

    private final UserHistoryWorkoutRepository userHistoryWorkoutRepository;


    public Optional<UserHistoryWorkout> getUserHistoryWorkout(Long Id)
    {
        return userHistoryWorkoutRepository.findById(Id);
    }


    public UserHistoryWorkout save(UserHistoryWorkout userHistoryWorkout) {

        return userHistoryWorkoutRepository.save(userHistoryWorkout);

    }
    public UserHistoryWorkout saveAndFlush(UserHistoryWorkout userHistoryWorkout) {

        return userHistoryWorkoutRepository.saveAndFlush(userHistoryWorkout);

    }

    public List<UserHistoryWorkout> getStartedUserHistoryWorkoutsByUserEmail(String emailUser) {

        return userHistoryWorkoutRepository.findStartedWorkoutsByUserEmail(emailUser);
    }

    public UserHistoryWorkout isWorkoutPresentInUserHistory(Long workoutId, String emailUser) throws NotFoundException {

        return userHistoryWorkoutRepository.findSpecificStartedWorkoutByUserEmail(workoutId,emailUser).orElseThrow(() ->new NotFoundException("workout with id " + workoutId+ " not found in user workout history of user with email "+ emailUser));
    }

    public UserHistoryWorkout findById(Long userHistoryWorkoutId) throws NotFoundException {
        return userHistoryWorkoutRepository.findById(userHistoryWorkoutId).orElseThrow(()-> new NotFoundException( "user history workout entry with id"+ userHistoryWorkoutId + " not found"));
    }

    public List<Long> findAllUserHistoryExercisesSecondsByUserHistoryWorkoutId(Long id, String email)
    {
        return userHistoryWorkoutRepository.findAllUserHistoryExercisesSecondsByUserHistoryWorkoutId(id,email);
    }

    public List<Double> findAllUserHistoryExercisesCaloriesByUserHistoryWorkoutId(Long id, String email)
    {
        return userHistoryWorkoutRepository.findAllUserHistoryExercisesCaloriesByUserHistoryWorkoutId(id,email);
    }



}
