package com.religion.zhiyun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@MapperScan("com.religion.zhiyun.**.dao")
@EnableScheduling
public class ZhiyunApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhiyunApplication.class, args);
    }

}
