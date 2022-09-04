package com.example.edge.config;

import util.FileUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BeanConfig {
    @Bean
    public FileUtil fileUtil(){
        return new FileUtil();
    }
}
