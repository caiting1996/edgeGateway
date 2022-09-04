package com.example.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.cloud.util.FileUtil;

@Configuration
public class BeanConfig {
    @Bean
    public FileUtil fileUtil(){
        return new FileUtil();
    }
}
