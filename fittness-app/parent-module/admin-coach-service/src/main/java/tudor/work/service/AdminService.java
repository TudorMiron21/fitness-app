package tudor.work.service;

import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.CoachDetailsResponseDto;
import tudor.work.model.CoachDetails;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MinioService minioService;
    private final CoachDetailsService coachDetailsService;

    public Set<CoachDetailsResponseDto> getAllInvalidatedCoachRequests() {

        Set<CoachDetails> coachDetailsSet = coachDetailsService.findAllByInvalidated();


        return coachDetailsSet.stream().map(coachDetails -> {

            try {
                return CoachDetailsResponseDto
                        .builder()
                        .id(coachDetails.getId())
                        .imageResourcePreSignedUrl(minioService.generatePreSignedUrl(coachDetails.getCoachCertificatePath()))
                        .user(coachDetails.getUser())
                        .certificationType(coachDetails.getCertificationType())
                        .isValidated(coachDetails.getIsValidated())
                        .build();
            } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     InternalException e) {
                throw new RuntimeException(e);
            }

        }).collect(Collectors.toSet());
    }

    @Transactional
    public void validateCoachRequest(Long idCoachDetails) throws NotFoundException {
        CoachDetails coachDetails = coachDetailsService.findById(idCoachDetails);
        coachDetails.setIsValidated(true);
    }

}
