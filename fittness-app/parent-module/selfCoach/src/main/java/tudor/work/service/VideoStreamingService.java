package tudor.work.service;

import io.minio.StatObjectResponse;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.exceptions.StorageException;
import tudor.work.model.Exercise;
import tudor.work.utils.Range;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class VideoStreamingService {

    private final MinioService minioService;

    private final ExerciseService exerciseService;
    private String determineContentType(String fileName) {

        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1).toLowerCase();
        }

        return switch (extension) {
            case "mp4" -> "video/mp4";
            case "mpeg" -> "video/mpeg";
            case "mov", "qt" -> "video/quicktime";
            case "wmv" -> "video/x-ms-wmv";
            case "avi" -> "video/x-msvideo";
            case "flv" -> "video/x-flv";
            case "webm" -> "video/webm";
            default -> "application/octet-stream";
        };
    }

    public ChunkWithMetadata fetchChunk(Long exerciseId, Range range) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        Exercise exercise = exerciseService.getExerciseById(exerciseId);

        String exerciseVideoLocation = exercise.getExerciseVideoUrl();
        String bucketName = exerciseVideoLocation.split("/")[0];
        String objectName = exerciseVideoLocation.split("/")[1];

        StatObjectResponse fileMetadata = minioService.getFileMetadata(bucketName, objectName);


        long startPosition = range.getRangeStart();
        long endPosition = range.getRangeEnd(fileMetadata.size());
        int chunkSize = (int) (endPosition - startPosition + 1);
        try (InputStream inputStream = minioService.getInputStream(objectName, startPosition, chunkSize)) {

            return new ChunkWithMetadata(fileMetadata.size(), determineContentType(objectName), inputStream.readAllBytes());
        } catch (Exception exception) {
            throw new StorageException(exception);
        }
    }

    public record ChunkWithMetadata(
            Long fileSize,
            String ContentType,
            byte[] chunk
    ) {
    }
}
