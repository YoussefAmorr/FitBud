package com.fitbud.backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMillis;

    public JwtService(
            @Value("${fitbud.jwt.secret}") String secret,
            @Value("${fitbud.jwt.expiration-ms}") long expirationMillis) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String email) {

        Date now = new Date();
        Date expiration = new Date(
                now.getTime() + expirationMillis
        );

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
}