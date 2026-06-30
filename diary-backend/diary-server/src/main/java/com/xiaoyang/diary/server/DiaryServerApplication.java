package com.xiaoyang.diary.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 * 如果你碰到启动的问题，请认真阅读 <a href="http://localhostquick-start/">...</a>
 */
@SpringBootApplication(scanBasePackages = {"${diary.info.base-package}.server", "${diary.info.base-package}.module"})
public class DiaryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiaryServerApplication.class, args);
    }

}
