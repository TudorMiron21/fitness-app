package tudor.work.service;


import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tudor.work.dto.CompleteMultipartUploadDto;
import tudor.work.dto.UploadCoachDetailsRequestDto;
import tudor.work.dto.UploadExerciseDetailsDto;
import tudor.work.model.CoachDetails;
import tudor.work.model.Exercise;
import tudor.work.model.User;
import tudor.work.utils.MinioMultipartUploadUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoachService {

    private final MinioService minioService;
    private final CoachDetailsService coachDetailsService;
    private final AuthorityService authorityService;
    private final MinioMultipartUploadUtils minioMultipartUploadUtils;
    private final ExerciseService exerciseService;
    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;


    public void uploadCoachDetails(UploadCoachDetailsRequestDto uploadCoachDetailsRequestDto) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, NotFoundException {

        User user = authorityService.getUser();

        String imgFileName = user.getId() + ".png";

        String imgPath = minioService.uploadImageToObjectStorage(uploadCoachDetailsRequestDto.getCertificateImg().getInputStream(),imgFileName,"certificates");

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


    public Map<String, Object> UploadExerciseDetailsAndInitMultipart(String bucketName, String contentType, UploadExerciseDetailsDto uploadExerciseDetailsDto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException//also add the UploadExerciseDto as a parameter
    {

        Exercise savedExercise = exerciseService.saveExercise(
                Exercise.
                        builder()
                        .name(uploadExerciseDetailsDto.getExerciseName())
                        .description(uploadExerciseDetailsDto.getDescription())
                        .equipment(uploadExerciseDetailsDto.getEquipment())
                        .muscleGroup(uploadExerciseDetailsDto.getMuscleGroup())
                        .difficulty(uploadExerciseDetailsDto.getDifficulty())
                        .category(uploadExerciseDetailsDto.getCategory())
//                        .exerciseImageStartUrl(minioService.uploadImageToObjectStorage(uploadExerciseDetailsDto.))
                        .build()

        );
        if (uploadExerciseDetailsDto.getVideoSize() > 0) {
            minioService.createBucket(bucketName);
            return minioMultipartUploadUtils
                    .initMultipartUpload(bucketName,
                            savedExercise.getId() + "." + uploadExerciseDetailsDto.getVideoExtension(),
                            generatePartCount(uploadExerciseDetailsDto.getVideoSize()), contentType);
        }
        return new HashMap<>();
    }

//    public Boolean uploadExercise(String bucketName, Integer partCount, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//
//        minioService.createBucket(bucketName);
//        Map<String, Object> initMultipartResponse = minioMultipartUploadUtils.initMultipartUpload(bucketName, "id_exercise.mp4", partCount, contentType);
//
//        return minioMultipartUploadUtils.mergeMultipartUploads(bucketName, "id_exercise.mp4", initMultipartResponse.get("uploadId").toString());
//
//    }
//


    public Boolean completeMultipartUpload(String bucketName, CompleteMultipartUploadDto completeMultipartUploadDto) {
        return minioMultipartUploadUtils.mergeMultipartUploads(bucketName, "id_exercise.mp4", completeMultipartUploadDto.getUploadId());
    }
}
