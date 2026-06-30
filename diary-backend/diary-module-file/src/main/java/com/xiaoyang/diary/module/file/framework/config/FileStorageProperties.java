package com.xiaoyang.diary.module.file.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "diary.file")
public class FileStorageProperties {

    private S3 s3 = new S3();

    @Data
    public static class S3 {

        private String bucket;

        private String region = "us-east-1";

        private String endpoint;

        private String accessKey;

        private String secretKey;

        private boolean pathStyleAccessEnabled = true;

        private Duration previewUrlExpire = Duration.ofMinutes(30);
    }
}
