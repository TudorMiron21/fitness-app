package tudor.work.service;


import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    public InputStream getInputStream(String objectName, long offset, long length) throws Exception {
        return minioClient.getObject(
                GetObjectArgs
                        .builder()
                        .bucket("exercise-videos")
                        .offset(offset)
                        .length(length)
                        .object(objectName)
                        .build());
    }

    public StatObjectResponse getFileMetadata(String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return  minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());

    }


    public String generatePreSignedUrl(String imgPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String[] parts = imgPath.split("/", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid image path");
        }

        String bucketName = parts[0];
        String imageFileName = parts[1];

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
