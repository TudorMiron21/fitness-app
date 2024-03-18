package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tudor.work.dto.ExerciseFilteredRequestDto;
import tudor.work.dto.ExerciseResponseDto;
import tudor.work.model.Exercise;
import tudor.work.repository.ExerciseRepository;
import tudor.work.specification.ExerciseSpecification;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {


    private final ExerciseRepository exerciseRepository;
    private final AuthorityService authorityService;


    public Exercise saveExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public Exercise getExerciseByid(Long exerciseId) throws NotFoundException {
        return exerciseRepository.findById(exerciseId).orElseThrow(() -> new NotFoundException("Exercise with id " + exerciseId + " not found"));
    }

    public Set<Exercise> getAllExercisesByAdderId(Long adderId) {
        return exerciseRepository.findAllByAdderId(adderId);
    }


    public Set<Exercise> getFilteredExercises(ExerciseFilteredRequestDto exerciseFilteredRequestDto) throws NotFoundException {

        Specification<Exercise> spec = Specification.where(null);

        if (exerciseFilteredRequestDto.getName() != null)
            spec = spec.and(ExerciseSpecification.nameLike(exerciseFilteredRequestDto.getName()));

        if (exerciseFilteredRequestDto.getMuscleGroupNames() != null)
            spec = spec.and(ExerciseSpecification.muscleGroupNameIn(exerciseFilteredRequestDto.getMuscleGroupNames()));

        if (exerciseFilteredRequestDto.getDifficultyNames() != null)
            spec = spec.and(ExerciseSpecification.difficultyNameIn(exerciseFilteredRequestDto.getDifficultyNames()));

        if (exerciseFilteredRequestDto.getCategoryNames() != null)
            spec = spec.and(ExerciseSpecification.categoryNameIn(exerciseFilteredRequestDto.getCategoryNames()));

        if (exerciseFilteredRequestDto.getEquipmentNames() != null)
            spec = spec.and(ExerciseSpecification.equipmentNameIn(exerciseFilteredRequestDto.getEquipmentNames()));

        if (exerciseFilteredRequestDto.getIsExercisePrivate() != null)
            if (exerciseFilteredRequestDto.getIsExercisePrivate())
                spec = spec
                        .and(ExerciseSpecification.isExerciseAdderEqual(authorityService.getUserId()))
                        .and(ExerciseSpecification.isExercisePrivateEqual(true));
            else
                spec = spec.and(ExerciseSpecification.isExercisePrivateEqual(false));

        return new HashSet<>(exerciseRepository.findAll(spec));
    }
}
