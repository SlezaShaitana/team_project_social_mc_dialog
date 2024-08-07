package com.project.mc_dialog.security;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class JwtUtils {

    public String getId(String token){
        return Jwts.parser().build()
                .parseSignedClaims(token).getPayload().getId();

    }

    public String getEmail(String token){
        return Jwts.parser().build()
                .parseSignedClaims(token).getPayload().get("email", String.class);

    }

    public List<String> getRoles(String token){
        return Jwts.parser().build()
                .parseSignedClaims(token).getPayload().get("roles", List.class);

    }
}
