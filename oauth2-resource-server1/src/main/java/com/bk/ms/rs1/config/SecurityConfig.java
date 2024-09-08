package com.bk.ms.rs1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers("/products/**").access("hasAuthority('SCOPE_products.read')")
                .anyRequest().authenticated()
                .and()
//                .oauth2ResourceServer(Customizer.withDefaults())
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }
}