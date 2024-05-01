package tudor.work.service;


import com.github.dockerjava.api.exception.NotFoundException;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.Achievement;
import tudor.work.repository.AchievementRepository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;

    private final MinioService minioService;

    private boolean needsConversion(String url) {
        if (url != null)
            return !(url.startsWith("http://") || url.startsWith("https://"));
        return false;
    }

    public Achievement convertAchievementCoverPhoto(Achievement achievement) {
        String achievementCoverPhotoUrl = achievement.getAchievementPicturePath();
        if (needsConversion(achievementCoverPhotoUrl)) {
            try {
                achievement.setAchievementPicturePath(minioService.generatePreSignedUrl(achievementCoverPhotoUrl));
            } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     InternalException e) {
                throw new RuntimeException(e);
            }
        }

        return achievement;
    }

    public List<Achievement> findAll() {
        return achievementRepository
                .findAll();
//                .stream()
//                .map(this::convertAchievementCoverPhoto)
//                .toList();
    }

    public Achievement findById(Long id) {
        return achievementRepository.findById(id).orElseThrow(() -> new NotFoundException("achievement with id" + id + "not found"));
    }

}
