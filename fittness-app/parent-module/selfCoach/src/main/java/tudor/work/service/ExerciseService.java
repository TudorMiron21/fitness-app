package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.ExerciseDto;
import tudor.work.model.Exercise;
import tudor.work.repository.ExerciseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;


    public List<ExerciseDto> getAllExclusiveExercises() {

        return exerciseRepository.findByIsExerciseExclusiveTrue().stream().map(
                exercise -> ExerciseDto
                        .builder()
                        .name(exercise.getName())
                        .description(exercise.getDescription())
                        .mediaUrl(exercise.getMediaUrl())
                        .isExerciseExclusive(exercise.isExerciseExclusive())
                        .category(exercise.getCategory())
                        .difficulty(exercise.getDifficulty())
                        .workouts(exercise.getWorkouts())
                        .build()
        ).collect(Collectors.toList());
    }

    public List<ExerciseDto> getAllUserExercises() {

        return exerciseRepository.findByIsExerciseExclusiveFalse().stream().map(
                exercise -> ExerciseDto
                        .builder()
                        .name(exercise.getName())
                        .description(exercise.getDescription())
                        .mediaUrl(exercise.getMediaUrl())
                        .isExerciseExclusive(exercise.isExerciseExclusive())
                        .category(exercise.getCategory())
                        .difficulty(exercise.getDifficulty())
                        .workouts(exercise.getWorkouts())
                        .build()
        ).collect(Collectors.toList());
    }

    public Optional<Exercise> getExerciseByName(String name) {

        return exerciseRepository.findByName(name);
    }

    public void saveExercise(Exercise exercise) {
        exerciseRepository.save(exercise);
    }

    public Exercise getExerciseReference(Long exerciseId)
    {
        return exerciseRepository.getOne(exerciseId);
    }


}
