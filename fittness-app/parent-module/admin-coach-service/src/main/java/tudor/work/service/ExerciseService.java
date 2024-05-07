package tudor.work.service;

import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tudor.work.dto.ExerciseFilteredRequestDto;
import tudor.work.dto.ExerciseResponseDto;
import tudor.work.model.Exercise;
import tudor.work.repository.ExerciseRepository;
import tudor.work.specification.ExerciseSpecification;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {


    private final ExerciseRepository exerciseRepository;
    private final AuthorityService authorityService;
    private final MinioService minioService;


    public Exercise saveExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public Exercise getExerciseByid(Long exerciseId) throws NotFoundException {
        return exerciseRepository.findById(exerciseId).orElseThrow(() -> new NotFoundException("Exercise with id " + exerciseId + " not found"));
    }

    private boolean needsConversion(String url) {
        if (url != null)
            return !(url.startsWith("http://") || url.startsWith("https://"));
        return false;
    }

    public Exercise convertExercisePhotos(Exercise exercise) {
        String startPhotoUrl = exercise.getExerciseImageStartUrl();
        String endPhotoUrl  =  exercise.getExerciseImageEndUrl();

            try {
                if(needsConversion(startPhotoUrl)) {
                    exercise.setExerciseImageStartUrl(minioService.generatePreSignedUrl(startPhotoUrl));
                }
                if(needsConversion(endPhotoUrl))
                {
                    exercise.setExerciseImageEndUrl(minioService.generatePreSignedUrl(endPhotoUrl));
                }
                return exercise;
            } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     InternalException e) {
                throw new RuntimeException(e);
            }


    }

    public List<Exercise> getAllExercisesByAdderId(Long adderId) {
        return exerciseRepository.findAllByAdderId(adderId).stream().map(this::convertExercisePhotos).toList();
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

        if (exerciseFilteredRequestDto.getIsExercisePrivate() != null) {
            if (exerciseFilteredRequestDto.getIsExercisePrivate())
                spec = spec
                        .and(ExerciseSpecification.isExerciseAdderEqual(authorityService.getUserId()))
                        .and(ExerciseSpecification.isExercisePrivateEqual(true));
            else
                spec = spec.and(ExerciseSpecification.isExercisePrivateEqual(false));
        }
        else
        {
            spec = spec.and(ExerciseSpecification.isExercisePrivateEqual(false));
        }


        return new HashSet<>(exerciseRepository.findAll(spec));
    }

    public Exercise findById(Long id) throws NotFoundException {
        return exerciseRepository.findById(id).orElseThrow(() -> new NotFoundException("exercise with id " + id + " not found"));
    }
}
