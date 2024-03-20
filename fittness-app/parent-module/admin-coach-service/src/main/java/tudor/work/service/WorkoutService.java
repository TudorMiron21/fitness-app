package tudor.work.service;


import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.springframework.stereotype.Service;
import tudor.work.model.Workout;
import tudor.work.repository.WorkoutRepository;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    public Workout saveWorkout(Workout workout)
    {
        return workoutRepository.save(workout);
    }

    public Workout findById(Long id) throws NotFoundException {
        return workoutRepository.findById(id).orElseThrow(()->new NotFoundException("workout with id "+id+ " not found"));
    }
}
