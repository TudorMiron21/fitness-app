package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.UploadExerciseDetailsDto;
import tudor.work.model.Exercise;
import tudor.work.repository.ExerciseRepository;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public Exercise saveExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

}
