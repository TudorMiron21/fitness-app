package tudor.work.utils;

import com.google.common.collect.Multimap;
import io.minio.CreateMultipartUploadResponse;
import io.minio.MinioClient;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioMultipartUploadClient extends MinioClient {
    protected MinioMultipartUploadClient(MinioClient client) {
        super(client);
    }

    public String initMultiPartUpload(String bucket, String region, String object, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) throws InternalException, InvalidResponseException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException {
        CreateMultipartUploadResponse response =  this.createMultipartUpload(bucket, region, object, headers, extraQueryParams);

        return response.result().uploadId();
    }




}
