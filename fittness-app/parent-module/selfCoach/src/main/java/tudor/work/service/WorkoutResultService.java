package tudor.work.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.WorkoutResult;
import tudor.work.repository.WorkoutResultRepository;

@RequiredArgsConstructor
@Service
public class WorkoutResultService {

    private final WorkoutResultRepository workoutResultRepository;

    public WorkoutResult save(WorkoutResult workoutResult)
    {
        return workoutResultRepository.save(workoutResult);
    }

}
