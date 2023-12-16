package tudor.work.service;


import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.model.User;
import tudor.work.model.Workout;
import tudor.work.repository.WorkoutRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    @Transactional
    public void saveWorkout(Workout workout) throws DuplicatesException {

        Optional<Workout> foundWorkout = workoutRepository.findByName(workout.getName());
        if (foundWorkout.isPresent()) {
            if (!foundWorkout.get().isGlobal()) {//this is used for when an admin posts a workout but a workout with the same name is already posted by a user
                workoutRepository.save(workout);
            } else {
                throw new DuplicatesException("workout already in database");
            }
        } else {
            workoutRepository.save(workout);
        }
    }

    public Optional<Workout> findWorkoutByName(String workoutName) {
        return workoutRepository.findByName(workoutName);
    }

    public Optional<Workout> findWorkoutById(Long id)
    {
        return workoutRepository.findById(id);
    }
    public Workout getReference(Long id) {
        return workoutRepository.getOne(id);
    }

    public void deleteWorkout(Workout workout) {
        workoutRepository.delete(workout);
    }

    public List<Workout> getAllWorkouts() {

        return workoutRepository.findAll();
    }

    public boolean isWorkoutLikedByUser(Workout workout,User user) throws NotFoundException {

        return workoutRepository
                .findById(workout.getId())
                .orElseThrow(()->new NotFoundException("workout " + workout.getName() + " not found"))
                .getLikers()
                .stream()
                .anyMatch(user1 -> user1.equals(user));
    }

    public Long getNoLikes(Workout workout)
    {
        return (long) workout.getLikers().size();
    }


}
