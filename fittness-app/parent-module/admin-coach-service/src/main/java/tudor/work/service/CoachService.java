package tudor.work.service;


import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xmlunit.diff.Diff;
import tudor.work.dto.*;
import tudor.work.model.CoachDetails;
import tudor.work.model.Exercise;
import tudor.work.model.User;
import tudor.work.repository.DifficultyRepository;
import tudor.work.repository.EquipmentRepository;
import tudor.work.utils.MinioMultipartUploadUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachService {

    private final MinioService minioService;
    private final CoachDetailsService coachDetailsService;
    private final AuthorityService authorityService;
    private final MinioMultipartUploadUtils minioMultipartUploadUtils;
    private final ExerciseService exerciseService;
    private final EquipmentService equipmentService;
    private final MuscleGroupService muscleGroupService;
    private final DifficultyService difficultyService;
    private final CategoryService categoryService;
    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;


    public void uploadCoachDetails(UploadCoachDetailsRequestDto uploadCoachDetailsRequestDto) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, NotFoundException {

        User user = authorityService.getUser();

        String imgFileName = user.getId() + ".png";

        String imgPath = minioService.uploadImageToObjectStorage(uploadCoachDetailsRequestDto.getCertificateImg().getInputStream(), imgFileName, "certificates");

        coachDetailsService.save(
                CoachDetails
                        .builder()
                        .user(user)
                        .coachCertificatePath(imgPath)
                        .certificationType(uploadCoachDetailsRequestDto.getCertificationType())
                        .yearsOfExperience(uploadCoachDetailsRequestDto.getYearsOfExperience())
                        .isValidated(false)
                        .build()
        );
    }

    public Boolean checkAreCoachDetailsValid() throws NotFoundException {

        authorityService.getUser();
        return authorityService.getUser().getCoachDetails().stream().anyMatch(CoachDetails::getIsValidated);
    }

    private Integer generatePartCount(Long fileSize) {
        long maxRequestSizeLong = Long.parseLong(maxRequestSize.replaceAll("[^0-9]", "")) * 1000000;
        return (int) ((fileSize + maxRequestSizeLong - 1) / maxRequestSizeLong);
    }

    public String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == 0) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }

    @Transactional
    public Map<String, Object> UploadExerciseDetailsAndInitMultipart(String bucketName, String contentType, UploadExerciseDetailsDto uploadExerciseDetailsDto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, NotFoundException//also add the UploadExerciseDto as a parameter
    {

        boolean hasReps = true;
        boolean hasWeight = true;
        if (uploadExerciseDetailsDto.getCategoryName().equals("Cardio")) {

            hasReps = false;
            hasWeight = false;
        }

        if (uploadExerciseDetailsDto.getEquipmentName().equals("Body Only")) {
            hasWeight = false;
        }

        Exercise savedExercise = exerciseService.saveExercise(
                Exercise.
                        builder()
                        .name(uploadExerciseDetailsDto.getExerciseName())
                        .description(uploadExerciseDetailsDto.getDescription())
                        .equipment(equipmentService.getEquipmentByName(uploadExerciseDetailsDto.getEquipmentName()))
                        .muscleGroup(muscleGroupService.getMuscleGroupByName(uploadExerciseDetailsDto.getMuscleGroupName()))
                        .difficulty(difficultyService.getDifficultyByName(uploadExerciseDetailsDto.getDifficultyName()))
                        .category(categoryService.getCategoryByName(uploadExerciseDetailsDto.getCategoryName()))
                        .adder(authorityService.getUser())
                        .isExerciseExclusive(true)
                        .hasWeight(hasWeight)
                        .hasNoReps(hasReps)
                        .build()
        );

        if (!uploadExerciseDetailsDto.getBeforeImage().isEmpty()) {
            minioService.createBucket("exercise-images");
            minioService.uploadImageToObjectStorage(
                    uploadExerciseDetailsDto.getBeforeImage().getInputStream(),
                    savedExercise.getId() + "_before." + this.getFileExtension(uploadExerciseDetailsDto.getBeforeImage().getOriginalFilename()),
                    "exercise-images"
            );
            String pathBeforeImage = "exercise-images/" + savedExercise.getId() + "_before." + this.getFileExtension(uploadExerciseDetailsDto.getBeforeImage().getOriginalFilename());
            savedExercise.setExerciseImageStartUrl(pathBeforeImage);
        }
        if (!uploadExerciseDetailsDto.getAfterImage().isEmpty()) {
            minioService.createBucket("exercise-images");
            minioService.uploadImageToObjectStorage(
                    uploadExerciseDetailsDto.getAfterImage().getInputStream(),
                    savedExercise.getId() + "_after." + this.getFileExtension(uploadExerciseDetailsDto.getAfterImage().getOriginalFilename()),
                    "exercise-images"
            );
            String pathAfterImage = "exercise-images/" + savedExercise.getId() + "_after." + this.getFileExtension(uploadExerciseDetailsDto.getAfterImage().getOriginalFilename());
            savedExercise.setExerciseImageEndUrl(pathAfterImage);
        }
        if (uploadExerciseDetailsDto.getVideoSize() > 0) {
            minioService.createBucket(bucketName);

            Map<String, Object> response = minioMultipartUploadUtils
                    .initMultipartUpload(bucketName,
                            savedExercise.getId() + "." + getFileExtension(uploadExerciseDetailsDto.getVideoName()),
                            generatePartCount(uploadExerciseDetailsDto.getVideoSize()), contentType);

            response.put("exerciseId", savedExercise.getId());

            return response;
        }
        return new HashMap<>();
    }


    @Transactional
    public Boolean completeMultipartUpload(String bucketName, CompleteMultipartUploadDto completeMultipartUploadDto) throws NotFoundException {


        Exercise uploadExercise = exerciseService.getExerciseByid(completeMultipartUploadDto.getExerciseId());

        String objectName = uploadExercise.getId() + "." + getFileExtension(completeMultipartUploadDto.getFilename());
        String videoPath = bucketName + "/" + objectName;

        uploadExercise.setExerciseVideoUrl(videoPath);

        return minioMultipartUploadUtils.mergeMultipartUploads(bucketName, objectName, completeMultipartUploadDto.getUploadId());
    }


    public Set<ExerciseResponseDto> getFilteredExercises(ExerciseFilteredRequestDto exerciseFilteredRequestDto) {
        Set<Exercise> exercises = exerciseService.getFilteredExercises(exerciseFilteredRequestDto);

        return exercises.stream().map(exercise ->
                ExerciseResponseDto
                        .builder()
                        .exerciseId(exercise.getId())
                        .name(exercise.getName())
                        //TODO: complete here for the case where the exercise is exclusive(it has a path saved, not the url)
                        .exerciseImageStartUrl(exercise.getExerciseImageStartUrl())
                        .muscleGroup(exercise.getMuscleGroup())
                        .equipment(exercise.getEquipment())
                        .difficulty(exercise.getDifficulty())
                        .category(exercise.getCategory())
                        .isExerciseExclusive(exercise.isExerciseExclusive())
                        .build()
        ).collect(Collectors.toSet());
    }
}



