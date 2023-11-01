package tudor.work.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.model.Workout;
import tudor.work.repository.WorkoutRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    @Transactional
    public void saveWorkout(Workout workout) throws DuplicatesException {

        if (workoutRepository.findByName(workout.getName()).isPresent()) {
            throw new DuplicatesException("workout already in database");
        } else {
            workoutRepository.save(workout);
        }
    }

    public Optional<Workout> findWorkoutByName(String workoutName) {
        return workoutRepository.findByName(workoutName);
    }

    public Workout getReference(Long id)
    {
        return workoutRepository.getOne(id);
    }

    public void deleteWorkout(Workout workout)
    {
        workoutRepository.delete(workout);
    }


}
