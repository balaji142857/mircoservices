package com.bk.ms.rs1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(c -> {
                    c.requestMatchers("/products/**")
                            .hasAuthority("SCOPE_products.read");
                    c.anyRequest().authenticated();
                })
                .oauth2ResourceServer(c -> c.jwt(Customizer.withDefaults()));
        return http.build();
    }
}