//package com.project.mc_dialog.security;
//
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//    private final JwtUtils jwtUtils;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            String token = getToken(request);
//            if (token != null && jwtUtils.validateToken(token)) {
//                Claims claims = jwtUtils.parseToken(token);
//                PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(claims, null);
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        } catch (Exception e) {
//
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    private String getToken(HttpServletRequest request) {
//        String headerAuth = request.getHeader("Authorization");
//
//        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
//            return headerAuth.substring(7);
//        }
//        return null;
//    }
//}
