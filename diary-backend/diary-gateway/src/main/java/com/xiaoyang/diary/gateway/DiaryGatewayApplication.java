package com.xiaoyang.diary.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class DiaryGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiaryGatewayApplication.class, args);
    }
}
