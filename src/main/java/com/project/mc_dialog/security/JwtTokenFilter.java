package com.project.mc_dialog.security;

import com.project.mc_dialog.feign.JwtValidation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final JwtValidation jwtValidation;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String account_id = null;
        List<String> roles = new ArrayList<>();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = getToken(authHeader);
            if (jwtValidation.validateToken(token)){
                log.info("JWT Token is valid");
                account_id = jwtUtils.getId(token);
                roles = jwtUtils.getRoles(token);
            } else {
                log.info("JWT token validation failed");
            }
        }

        if (request.getRequestURI().equals("/api/v1/streaming/ws")) {
            final Cookie[] cookies = request.getCookies();
            account_id = "123";
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("jwt")) {
                        account_id = jwtUtils.getId(cookie.getValue());
                        roles = List.of("USER");
                    }
                }
            }
        }

        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        if (account_id != null && !roles.isEmpty()) {
            UsernamePasswordAuthenticationToken springContextToken;
            springContextToken = new UsernamePasswordAuthenticationToken(account_id, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(springContextToken);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
