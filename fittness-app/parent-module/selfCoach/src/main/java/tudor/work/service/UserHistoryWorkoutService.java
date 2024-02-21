package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.*;
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

    public List<Long> findAllUserHistoryExercisesSecondsByUserHistoryWorkoutId(Long id)
    {
        return userHistoryWorkoutRepository.findAllUserHistoryExercisesSecondsByUserHistoryWorkoutId(id);
    }

    public List<Double> findAllUserHistoryExercisesCaloriesByUserHistoryWorkoutId(Long id)
    {
        return userHistoryWorkoutRepository.findAllUserHistoryExercisesCaloriesByUserHistoryWorkoutId(id);
    }


    public List<Category> findAllCategoriesByUserHistoryWorkoutId(Long id) {
        return userHistoryWorkoutRepository.findAllCategoriesByUserHistoryWorkoutId(id);
    }

    public List<Difficulty> findAllDifficultiesByUserHistoryWorkoutId(Long id)
    {
        return userHistoryWorkoutRepository.findAllDifficultiesByUserHistoryWorkoutId(id);
    }

    public List<MuscleGroup> findAllMuscleGroupsByUserHistoryWorkoutId(Long id)
    {
        return userHistoryWorkoutRepository.findAllMuscleGroupsByUserHistoryWorkoutId(id);
    }


    public List<UserHistoryExercise> findAllUserHistoryExercisesByUserHistoryWorkoutId(Long userHistoryWorkoutId) {
        return userHistoryWorkoutRepository.findAllUserHistoryExercisesByUserHistoryWorkoutId(userHistoryWorkoutId);
    }

    public List<Double> findAllUserHistoryVolumesWeightsByUserHistoryWorkoutId(Long userHistoryWorkoutId) {

        return userHistoryWorkoutRepository.findAllUserHistoryVolumesWeightsByUserHistoryWorkoutId(userHistoryWorkoutId);
    }
}
