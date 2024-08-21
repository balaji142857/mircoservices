package com.bk.ms.orderservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@Slf4j
public class WebConfig {


    @Value("${web.outbound.timeouts.read}")
    Integer readTimeout;

    @Value("${web.outbound.timeouts.connection}")
    Integer connectTimeout;


    @Bean
    RestTemplate restTemplate() {
        var template =  new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(connectTimeout))
                .setReadTimeout(Duration.ofSeconds(readTimeout))
                .build();
        log.info("RestTemplate constructed with readTimeout: {} connectTimeout {}",
                readTimeout, connectTimeout);
        return template;
    }
}
