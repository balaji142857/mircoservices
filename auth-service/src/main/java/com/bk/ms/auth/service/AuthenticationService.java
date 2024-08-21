package com.bk.ms.auth.service;

import com.bk.ms.auth.model.CustomUserDetails;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService userDetailService;


    public CustomUserDetails signup(CustomUserDetails input) {
        return userDetailService.createActiveUser(input.getUsername(), passwordEncoder.encode("password"));
//        return userDetailService.getUserByName(input.getUsername());
    }

    public CustomUserDetails authenticate(CustomUserDetails input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );
        return userDetailService.getUserByName(input.getUsername());
    }

    public List<String> allUsers() {
        return userDetailService.getUsers();
    }

    @PostConstruct
    void init() {
        userDetailService.createActiveUser("balaji", passwordEncoder.encode("password"));
    }
}