package com.bk.ms.auth.service;

import com.bk.ms.auth.model.CustomUserDetails;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final Map<String, CustomUserDetails> inMemoryUserDetails = new ConcurrentHashMap<>();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user =  inMemoryUserDetails.get(username);
        if (null == user) {
            throw new UsernameNotFoundException("Unable to find user "+ username);
        }
        return user;
    }



    CustomUserDetails createActiveUser(String user, String encodedPassword) {
        var newUser =  new CustomUserDetails(user, encodedPassword, true, true, true, true, List.of());
        inMemoryUserDetails.put(user, newUser);
        return newUser;
    }

    CustomUserDetails getUserByName(String username) {
        return inMemoryUserDetails.entrySet().stream()
                .filter(e -> null != e.getKey() && e.getKey().equals(username))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);

    }

    public List<String> getUsers() {
        return inMemoryUserDetails.values().stream()
                .filter(Objects::nonNull)
                .map(CustomUserDetails::getUsername)
                .toList();
    }




}

