package com.religion.zhiyun.utils.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class MyConfig implements WebMvcConfigurer {
   // @Value("${upload.path}")
    private String uploadPath;

   /* @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        *//**
         * 访问路径：http://localhost:93/facereport/static/captured/daping_report/captured_images/face/788001414206074880/2021/1/27/cj_142207194912440320.png
         * "/facereport/static/captured/**" 为前端URL访问路径
         * "file:" + uploadPath 是本地磁盘映射
         *//*
        registry.addResourceHandler("/image/static/**").addResourceLocations("file:" + uploadPath);
    }*/

}

/*import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyConfig implements WebMvcConfigurer {
    *//*
     *addResourceHandler:访问映射路径
     *addResourceLocations:资源绝对路径
     *//*
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/product/**").addResourceLocations("file:D:/secondhansImage/");
    }
}*/
