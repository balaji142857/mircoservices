package com.ms.bk.ms.webapp;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class OAuthAuthCodeFlowApplication {


    @Value("${SPRING_DEMO_GITHUB_CLIENT_ID}")
    String clientId;

    @Value("${SPRING_DEMO_GITHUB_CLIENT_SECRET}")
    String clientSecret;
    public static void main(String[] args) {
        SpringApplication.run(OAuthAuthCodeFlowApplication.class, args);
    }


    @PostConstruct
    void init() {
        log.warn("loaded client registration values form env: {} {}", clientId, clientSecret);
    }
}
