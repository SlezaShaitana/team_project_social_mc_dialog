package com.project.mc_dialog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.Password;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String secret;

    public String generateToken(String username) {

        return Jwts.builder()
                .id(username)
                .signWith(createSecretKey(secret))
                .compact();
    }

    public String getId(String token){

        return Jwts.parser().verifyWith(createSecretKey(secret)).build()
                .parseSignedClaims(token).getPayload().getId();

    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(createSecretKey(secret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(createSecretKey(secret)).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static SecretKey createSecretKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }

}
