package com.iflytek.asrc.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Appconfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}