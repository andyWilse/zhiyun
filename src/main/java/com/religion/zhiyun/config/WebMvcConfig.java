package com.religion.zhiyun.config;

import com.religion.zhiyun.config.Interceptor.LogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("profile/**").addResourceLocations("file:D:/upload/");
    }

  /*  @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }*/

    @Bean
    public LogInterceptor getLogInterceptor() {
        return new LogInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLogInterceptor());

        /*InterceptorRegistration registration = registry.addInterceptor(loginInterceptor());
        registration.addPathPatterns("/**");
        registration.excludePathPatterns("/index");*/
        /*registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login.jsp")
                .excludePathPatterns("/api/ajaxLogin")
                .excludePathPatterns("/logout");*/
    }

}
