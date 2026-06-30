package com.xiaoyang.diary.framework.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "diary.mq")
public class DiaryMqProperties {

    private Duration idempotentTtl = Duration.ofDays(7);

}
