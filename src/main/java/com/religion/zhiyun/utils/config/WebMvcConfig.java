package com.religion.zhiyun.utils.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configurable
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("profile/**").addResourceLocations("file:D:/upload/");
    }
}
