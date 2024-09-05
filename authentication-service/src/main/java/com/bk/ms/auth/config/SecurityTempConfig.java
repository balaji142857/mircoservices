package com.bk.ms.auth.config;

import com.bk.ms.auth.service.LdapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityTempConfig {

    @Value("${app.request.header.userId}")
    private String userIdHeader;

    @Bean
    public SecurityFilterChain filterChain(LdapService ldapService, HttpSecurity http) throws Exception {
        var filter = createFilter(ldapService, userIdHeader);
        http
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasAuthority("ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/user/**")).hasAuthority("USER")
                                .requestMatchers(new AntPathRequestMatcher("/test/**")).permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
//                .httpBasic(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults());
        return http.build();
    }

    private GenericFilterBean createFilter(LdapService ldapService, String userIdHeader) {
        var filter = new  MyCustomSecurityFilter(ldapService);
        filter.setUserIdHeader(userIdHeader);
        log.info("Custom Security Filter configured to look for {} header", userIdHeader);
        return filter;
    }

//    @Bean
//    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        return new InMemoryUserDetailsManager(
//                User.withUsername("balaji")
//                        .password(passwordEncoder.encode("pwd"))
//                        .roles("ADMIN","USER")
//                        .build(),
//                User.withDefaultPasswordEncoder()
//                        .username("krishnan")
//                        .password(passwordEncoder.encode("pwd"))
//                        .roles("ADMIN","USER")
//                        .build()
//        );
//    }
//
//
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }


    // TODO AUTH entry point & AUTH success handler



//    @Autowired
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .ldapAuthentication()
//                .userDnPatterns("uid={0},ou=people")
//                .groupSearchBase("ou=groups")
//                .contextSource()
//                .url("ldap://localhost:389/dc=balajikrishnan,dc=com")
//                .and()
//                .passwordCompare()
//                .passwordEncoder(new BCryptPasswordEncoder())
//                .passwordAttribute("userPassword");
//    }
}
