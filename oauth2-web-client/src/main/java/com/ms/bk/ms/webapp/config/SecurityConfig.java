package com.ms.bk.ms.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    HttpStatusEntryPoint unAuthorizedEntryPoint = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a -> {
                    // TODO /index.html to be incldued ?
                    a.requestMatchers("/", "/error", "/webjars/**" ,"/index.html").permitAll();
                    a.anyRequest().authenticated();
                })
//                .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .csrf(c -> {
                    var repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
                    repository.setCookieName("XSRF-TOKEN");
                    c.csrfTokenRepository(repository);
                })
                .logout(l -> l.logoutSuccessUrl("/").permitAll())
            .exceptionHandling(e -> e.authenticationEntryPoint(unAuthorizedEntryPoint))
          .oauth2Login(o -> o.failureHandler((req, resp, ex) -> req.getSession().setAttribute("error.message", ex.getMessage())));
        return http.build();
    }

}