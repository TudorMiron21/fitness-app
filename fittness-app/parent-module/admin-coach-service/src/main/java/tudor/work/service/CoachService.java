package tudor.work.service;


import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final CoachDetailsService coachDetailsService;
    private final AuthorityService authorityService;
    public void uploadCoachDetails(UploadCoachDetailsRequestDto uploadCoachDetailsRequestDto) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, NotFoundException {

        User user = authorityService.getUser();

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

    public Boolean checkAreCoachDetailsValid() throws NotFoundException {

        authorityService.getUser();
        return authorityService.getUser().getCoachDetails().stream().anyMatch(CoachDetails::getIsValidated);
    }
}
