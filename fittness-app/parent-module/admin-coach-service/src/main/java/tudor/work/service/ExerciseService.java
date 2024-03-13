package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.Exercise;
import tudor.work.repository.ExerciseRepository;

@Service
@RequiredArgsConstructor
public class ExerciseService {


    private final ExerciseRepository exerciseRepository;

    public Exercise saveExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public Exercise getExerciseByid(Long exerciseId) throws NotFoundException {
        return exerciseRepository.findById(exerciseId).orElseThrow(()-> new NotFoundException("Exercise with id "+exerciseId+" not found"));
    }
}
