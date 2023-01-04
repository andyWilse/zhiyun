package com.religion.zhiyun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.religion.zhiyun.*.dao")
public class ZhiyunApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhiyunApplication.class, args);
    }

}
