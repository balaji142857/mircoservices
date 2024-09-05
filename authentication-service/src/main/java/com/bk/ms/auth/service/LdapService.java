package com.bk.ms.auth.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class LdapService {

    Map<String, Collection<GrantedAuthority>> mockLdap = Map.ofEntries(
            Map.entry("balaji", List.of(new SimpleGrantedAuthority("USER"),new SimpleGrantedAuthority("ADMIN"))),
            Map.entry("ganga", List.of(new SimpleGrantedAuthority("USER"),new SimpleGrantedAuthority("ADMIN"))),
            Map.entry("krishnan", List.of(new SimpleGrantedAuthority("USER"))),
            Map.entry("divya", List.of(new SimpleGrantedAuthority("USER")))
    );


    public Collection<GrantedAuthority> getAuthorities(String userId) throws UsernameNotFoundException {
        if (!StringUtils.hasText(userId) || !mockLdap.containsKey(userId)) {
            throw new UsernameNotFoundException(userId + " not found in the underlying user store.");
        }
        return mockLdap.get(userId);
    }
}
