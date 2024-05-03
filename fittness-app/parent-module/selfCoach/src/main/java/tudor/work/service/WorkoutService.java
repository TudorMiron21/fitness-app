package tudor.work.service;


import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tudor.work.dto.FilterSearchDto;
import tudor.work.exceptions.DuplicatesException;
import tudor.work.model.User;
import tudor.work.model.Workout;
import tudor.work.model.WorkoutProgram;
import tudor.work.repository.WorkoutRepository;
import tudor.work.specification.WorkoutSpecification;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final MinioService minioService;

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

    public Optional<Workout> findWorkoutById(Long id) {
        return workoutRepository.findById(id);
    }

    public Workout getReference(Long id) {
        return workoutRepository.getOne(id);
    }

    public void deleteWorkout(Workout workout) {
        workoutRepository.delete(workout);
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

        workout
                .getExercises()
                .stream()
                .map(
                        exercise -> {
                            String exerciseCoverPhotoUrlStart = exercise.getExerciseImageStartUrl();
                            String exerciseCoverPhotoUrlEnd = exercise.getExerciseImageEndUrl();

                            if (needsConversion(exerciseCoverPhotoUrlStart)) {
                                try {
                                    exercise.setExerciseImageStartUrl(minioService.generatePreSignedUrl(exerciseCoverPhotoUrlStart));
                                } catch (ServerException | InsufficientDataException |
                                         ErrorResponseException | IOException |
                                         NoSuchAlgorithmException | InvalidKeyException |
                                         InvalidResponseException | XmlParserException |
                                         InternalException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            if (needsConversion(exerciseCoverPhotoUrlEnd)) {
                                try {
                                    exercise.setExerciseImageEndUrl(minioService.generatePreSignedUrl(exerciseCoverPhotoUrlEnd));
                                } catch (ServerException | InsufficientDataException |
                                         ErrorResponseException | IOException |
                                         NoSuchAlgorithmException | InvalidKeyException |
                                         InvalidResponseException | XmlParserException |
                                         InternalException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            return exercise;
                        }
                ).collect(Collectors.toSet());
        return workout;
    }

    public List<Workout> getAllWorkouts() {
        return workoutRepository
                .findAll()
                .stream()
                .map(this::convertWorkoutCoverPhotos)
                .toList();
    }

    public boolean isWorkoutLikedByUser(Workout workout, User user) throws NotFoundException {

        return workoutRepository
                .findById(workout.getId())
                .orElseThrow(() -> new NotFoundException("workout " + workout.getName() + " not found"))
                .getLikers()
                .stream()
                .anyMatch(user1 -> user1.equals(user));
    }

    public Long getNoLikes(Workout workout) {
        return (long) workout.getLikers().size();
    }


    public Integer getNumberOfWorkoutsForCoach(User coach) {
        return workoutRepository.findAllByAdder(coach).size();
    }

    public List<Workout> findAllByAdder(User coach) {
        return workoutRepository.findAllByAdder(coach)
                .stream()
                .map(this::convertWorkoutCoverPhotos)
                .toList();
    }


    public List<Workout> getFilteredWorkouts(FilterSearchDto filterSearchDto) {
        Specification<Workout> spec = Specification.where(null);

        if (filterSearchDto.getName() != null) {
            spec = spec.and(WorkoutSpecification.nameLike(filterSearchDto.getName()));
        }


        if (filterSearchDto.getMaxDifficulty() != null
                && filterSearchDto.getMinDifficulty() != null) {
            spec = spec.and(WorkoutSpecification.isWorkoutDifficultyLevelBetween(
                            filterSearchDto.getMinDifficulty(),
                            filterSearchDto.getMaxDifficulty()
                    )
            );

        }
        return workoutRepository.findAll(spec)
                .stream()
                .map(this::convertWorkoutCoverPhotos)
                .toList();
    }

}
