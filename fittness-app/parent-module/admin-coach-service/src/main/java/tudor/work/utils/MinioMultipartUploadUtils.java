package tudor.work.utils;

import com.google.common.collect.HashMultimap;
import com.netflix.discovery.converters.Auto;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListPartsResponse;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
//https://github.com/tuine/minio-multipart-upload/tree/main
public class MinioMultipartUploadUtils {

    private final MinioClient minioClient;


    public Map<String, Object> initMultipartUpload(String bucketName, String objectName, Integer partCount, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Map<String, Object> result = new HashMap<>();

        if (contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        HashMultimap<String, String> headers = HashMultimap.create();
        headers.put("Content-Type", contentType);

        MinioMultipartUploadClient minioMultipartUploadClient = new MinioMultipartUploadClient(minioClient);

        String uploadId = minioMultipartUploadClient.initMultiPartUpload(bucketName, null, objectName, headers, null);

        result.put("uploadId", uploadId);

        Map<String, String> reqParams = new HashMap<>();

        reqParams.put("uploadId", uploadId);
        List<String> partList = new ArrayList<>();

        for (int i = 0; i < partCount; i++) {
            reqParams.put("partNumber", String.valueOf(i + 1));
            String uploadUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(1, TimeUnit.DAYS)
                            .extraQueryParams(reqParams)
                            .build());
            partList.add(uploadUrl);

        }

        result.put("uploadUrls", partList);

        return result;
    }


    public Boolean mergeMultipartUploads(String bucketName, String objectName, String uploadId) {

        try {
            Part[] parts = new Part[1000];

            MinioMultipartUploadClient minioMultipartUploadClient = new MinioMultipartUploadClient(minioClient);

            ListPartsResponse partResult = minioMultipartUploadClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);

            int partNumber = 0;
            for (Part part : partResult.result().partList()) {
                parts[partNumber] = new Part(partNumber + 1, part.etag());
                partNumber++;
            }
            minioMultipartUploadClient.mergeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | InsufficientDataException |
                 ServerException | InternalException | XmlParserException | InvalidResponseException |
                 ErrorResponseException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }


}
