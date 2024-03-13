package tudor.work.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tudor.work.dto.UploadCoachDetailsRequestDto;
import tudor.work.model.CoachDetails;
import tudor.work.model.User;

import java.awt.desktop.UserSessionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final UserService userService;
    private final CoachDetailsService coachDetailsService;

    public void createBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {


        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(bucketName)
                            .build()
            );
        }
    }


    //  this method takes as parameter the image as stream and the image file name and should return the path to the image on the minio server
    public String uploadImageToObjectStorage(InputStream imgStream, String object, String bucket) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        createBucket(bucket);

        minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(bucket)
                        .object(object)
                        .stream(imgStream, -1, 10485760)
                        .contentType("image/png")
                        .build()
        );
        return bucket + "/" + object;
    }

    public InputStream getCertificateImage(String imgPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String[] parts = imgPath.split("/", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid image path");
        }
        String bucketName = parts[0]; // "certificates"
        String imageFileName = parts[1]; // "1.png"

        return minioClient.getObject(
                GetObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(imageFileName)
                        .build()
        );

    }

    public String generatePreSignedUrl(String imgPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String[] parts = imgPath.split("/", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid image path");
        }

        String bucketName = parts[0]; // "certificates"
        String imageFileName = parts[1]; // "1.png"

        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(imageFileName)
                        .expiry(1800)
                        .build()
        );
    }


}
