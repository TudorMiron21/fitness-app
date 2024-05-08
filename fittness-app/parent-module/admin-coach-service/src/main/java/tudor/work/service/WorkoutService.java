package tudor.work.service;


import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tudor.work.dto.WorkoutDto;
import tudor.work.dto.WorkoutFilteredRequestDto;
import tudor.work.model.Exercise;
import tudor.work.model.Workout;
import tudor.work.repository.WorkoutRepository;
import tudor.work.specification.WorkoutSpecification;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final AuthorityService authorityService;
    private final MinioService minioService;

    public Workout saveWorkout(Workout workout) {
        return workoutRepository.save(workout);
    }

    public Workout findById(Long id) throws NotFoundException {
        return workoutRepository.findById(id).orElseThrow(() -> new NotFoundException("workout with id " + id + " not found"));
    }

    public Set<Workout> getFilteredWorkouts(WorkoutFilteredRequestDto workoutFilteredRequest) throws NotFoundException {
        Specification<Workout> spec = Specification.where(null);

        if (workoutFilteredRequest.getName() != null) {
            spec = spec.and(WorkoutSpecification.nameLike(workoutFilteredRequest.getName()));
        }

        if (workoutFilteredRequest.getIsWorkoutPrivate() != null) {
            if (workoutFilteredRequest.getIsWorkoutPrivate())
                spec = spec.and(WorkoutSpecification.isWorkoutAdderEqual(authorityService.getUserId())).and(WorkoutSpecification.isWorkoutPrivateEqual(true));
            else
                spec = spec.and(WorkoutSpecification.isWorkoutPrivateEqual(false));
        } else {
            spec = spec.and(WorkoutSpecification.isWorkoutPrivateEqual(false));
        }

        if (workoutFilteredRequest.getMaxDifficulty() != null
                && workoutFilteredRequest.getMinDifficulty() != null) {
            spec = spec.and(WorkoutSpecification.isWorkoutDifficultyLevelBetween(
                            workoutFilteredRequest.getMinDifficulty(),
                            workoutFilteredRequest.getMaxDifficulty()
                    )
            );


        }
        return new HashSet<>(workoutRepository.findAll(spec));
    }

    private boolean needsConversion(String url) {
        if (url != null)
            return !(url.startsWith("http://") || url.startsWith("https://"));
        return false;
    }


    private Workout convertWorkoutCoverPhotos(Workout workout) {
        String workoutCoverPhotoUrl = workout.getCoverPhotoUrl();
        if (needsConversion(workoutCoverPhotoUrl)) {
            try {
                workout.setCoverPhotoUrl(minioService.generatePreSignedUrl(workoutCoverPhotoUrl));
            } catch (ServerException | InsufficientDataException | ErrorResponseException |
                     IOException | NoSuchAlgorithmException | InvalidKeyException |
                     InvalidResponseException | XmlParserException | InternalException e) {
                throw new RuntimeException(e);
            }
        }

        return workout;
    }

    public List<Workout> getAllWorkoutsByAdderId(Long userId) {
        return workoutRepository.findAllByAdderId(userId).stream().map(this::convertWorkoutCoverPhotos).toList();
    }

    public List<Workout> findByExercisesContaining(Exercise exercise)
    {
        return workoutRepository.findByExercisesContaining(exercise);
    }

    public Workout save(Workout workout) {
       return workoutRepository.save(workout);
    }

    public void delete(Workout workout)
    {
        workoutRepository.delete(workout);
    }

}
