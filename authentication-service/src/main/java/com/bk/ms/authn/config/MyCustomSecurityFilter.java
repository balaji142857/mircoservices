package com.bk.ms.authn.config;


import com.bk.ms.authn.service.LdapService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
public class MyCustomSecurityFilter extends GenericFilterBean {

    private final LdapService ldapService;


    @Getter
    @Setter
    private String userIdHeader;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var req = (HttpServletRequest)servletRequest;
        var userId = req.getHeader(userIdHeader);
        log.info("User id obtained from request header {} ", userId);
        if (!StringUtils.hasText(userId)) {
            log.warn("Received request without user id header for path: {}, headers: {}", req.getRequestURI(), req.getHeaderNames());
        } else {
            Collection<GrantedAuthority> authorityCollection = ldapService.getAuthorities(userId);
            var token = new UsernamePasswordAuthenticationToken(userId,"", authorityCollection);
            SecurityContextHolder.getContext().setAuthentication(token);
            log.info("token updated in security context: {}", token);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
