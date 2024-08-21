package com.bk.ms.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordEncoderService {

    private final PasswordEncoder encoder;

    String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
}
