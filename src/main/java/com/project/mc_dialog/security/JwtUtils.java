package com.project.mc_dialog.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String secret;


    public String getId(String token){
        return Jwts.parser().verifyWith(createSecretKey(secret)).build()
                .parseSignedClaims(token).getPayload().get("id", String.class);

    }

    public String getEmail(String token){
        return Jwts.parser().verifyWith(createSecretKey(secret)).build()
                .parseSignedClaims(token).getPayload().get("email", String.class);

    }

    public List<String> getRoles(String token){
        return Jwts.parser().verifyWith(createSecretKey(secret)).build()
                .parseSignedClaims(token).getPayload().get("roles", List.class);

    }

    public static SecretKey createSecretKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
