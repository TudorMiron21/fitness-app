package tudor.work.service;

import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tudor.work.dto.AchievementRequestDto;
import tudor.work.dto.CoachDetailsResponseDto;
import tudor.work.model.Achievement;
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
    private final AchievementService achievementService;

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
                     InternalException  e) {
                throw new RuntimeException(e);
            }

        }).collect(Collectors.toSet());
    }

    @Transactional
    public void validateCoachRequest(Long idCoachDetails) throws NotFoundException {
        CoachDetails coachDetails = coachDetailsService.findById(idCoachDetails);
        coachDetails.setIsValidated(true);
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
    public Long addAchievement(AchievementRequestDto achievementRequestDto) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        String imgName = RandomString.make(45) + "." + this.getFileExtension(achievementRequestDto.getAchievementPicture().getOriginalFilename());

        minioService.createBucket("achievement-photos");

        minioService.uploadImageToObjectStorage(
                achievementRequestDto.getAchievementPicture().getInputStream(),
                imgName,
                "achievement-photos"
        );

        String imgPath = "achievement-photos/" + imgName;

        Achievement achievement =
                Achievement
                        .builder()
                        .name(achievementRequestDto.getName())
                        .numberOfExercisesMilestone(achievementRequestDto.getNumberOfExercisesMilestone())
                        .numberOfWorkoutsMileStone(achievementRequestDto.getNumberOfWorkoutsMileStone())
                        .numberOfProgramsMilestone(achievementRequestDto.getNumberOfProgramsMilestone())
                        .numberOfChallengesMilestone(achievementRequestDto.getNumberOfChallengesMilestone())
                        .description(achievementRequestDto.getDescription())
                        .numberOfPoints(achievementRequestDto.getNumberOfPoints())
                        .achievementPicturePath(imgPath)
                        .build();

       Achievement fetchedAchievement = achievementService.save(achievement);

       return fetchedAchievement.getId();
    }
}
