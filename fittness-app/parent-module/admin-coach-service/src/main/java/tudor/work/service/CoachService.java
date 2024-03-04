package tudor.work.service;


import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.UploadCoachDetailsRequestDto;
import tudor.work.model.CoachDetails;
import tudor.work.model.User;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class CoachService {

    private final MinioService minioService;
    private final UserService userService;
    private final CoachDetailsService coachDetailsService;
    public void uploadCoachDetails(UploadCoachDetailsRequestDto uploadCoachDetailsRequestDto) throws  IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        User user = null;
        try {
            user = userService.getUserByEmail(uploadCoachDetailsRequestDto.getEmailUser());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        String imgFileName = user.getId() + ".png";

        String imgPath =  minioService.uploadCertificateImage(uploadCoachDetailsRequestDto.getCertificateImg().getInputStream(),imgFileName);

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
}
