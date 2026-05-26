package com.cenimarket.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/images/** 요청을 프로젝트 루트의 uploads/images 폴더와 연결한다.
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:uploads/images/");
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:uploads/profiles/");
    }
}
