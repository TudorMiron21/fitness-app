package tudor.work.service;

import com.github.dockerjava.api.exception.NotFoundException;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.ProgramDto;
import tudor.work.model.Program;
import tudor.work.model.User;
import tudor.work.model.UserHistoryProgram;
import tudor.work.repository.ProgramRepository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final MinioService minioService;


    public Program findById(Long programId) {
        return programRepository.findById(programId).orElseThrow(() -> new NotFoundException("program with id " + programId + " does not exist"));
    }

    private boolean needsConversion(String url) {
        if (url != null)
            return !(url.startsWith("http://") || url.startsWith("https://"));
        return false;
    }

    private Program convertProgramCoverPhotos(Program program) {
        String programCoverPhotoUrl = program.getCoverPhotoUrl();
        if (needsConversion(programCoverPhotoUrl)) {
            try {
                program.setCoverPhotoUrl(minioService.generatePreSignedUrl(programCoverPhotoUrl));
            } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     InternalException e) {
                throw new RuntimeException(e);
            }
        }

        program
                .getWorkoutPrograms()
                .stream()
                .map(
                        workoutProgram ->
                        {
                            String workoutCoverPhotoUrl = workoutProgram.getWorkout().getCoverPhotoUrl();

                            if (needsConversion(workoutCoverPhotoUrl)) {
                                try {
                                    workoutProgram.getWorkout().setCoverPhotoUrl(minioService.generatePreSignedUrl(workoutCoverPhotoUrl));
                                } catch (ServerException | InsufficientDataException | ErrorResponseException |
                                         IOException | NoSuchAlgorithmException | InvalidKeyException |
                                         InvalidResponseException | XmlParserException | InternalException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            workoutProgram
                                    .getWorkout()
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
                            return workoutProgram;
                        }
                ).collect(Collectors.toSet());

        return program;
    }

    public List<Program> findAllPrograms() {

        return programRepository
                .findAll()
                .stream()
                .map(this::convertProgramCoverPhotos)
                .toList();

    }

    public Integer getNumberOfProgramsForCoach(User coach) {
        return programRepository.findAllByAdder(coach).size();
    }
}
