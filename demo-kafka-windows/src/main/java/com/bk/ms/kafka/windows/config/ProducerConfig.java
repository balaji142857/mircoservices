package com.bk.ms.kafka.windows.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerConfig {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
