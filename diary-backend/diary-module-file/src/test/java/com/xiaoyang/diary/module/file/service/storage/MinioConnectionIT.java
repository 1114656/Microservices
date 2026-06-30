package com.xiaoyang.diary.module.file.service.storage;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class MinioConnectionIT {

    private static final String DEFAULT_ENDPOINT = "http://112.192.20.218:9000";
    private static final String DEFAULT_BUCKET = "diary-file";

    @Test
    void shouldConnectAndCreateBucketWhenMissing() {
        String endpoint = env("MINIO_ENDPOINT", DEFAULT_ENDPOINT);
        String accessKey = env("MINIO_ACCESS_KEY", "");
        String secretKey = env("MINIO_SECRET_KEY", "");
        String bucket = env("MINIO_BUCKET", DEFAULT_BUCKET);

        assumeTrue(!accessKey.isBlank() && !secretKey.isBlank(),
                "Set MINIO_ACCESS_KEY and MINIO_SECRET_KEY to run this integration test");

        try (S3Client client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build()) {
            ensureBucket(client, bucket);

            assertDoesNotThrow(() -> client.headBucket(HeadBucketRequest.builder()
                    .bucket(bucket)
                    .build()));
        }
    }

    private void ensureBucket(S3Client client, String bucket) {
        try {
            client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (S3Exception ex) {
            if (ex.statusCode() != 404) {
                throw ex;
            }
            client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        }
    }

    private String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
