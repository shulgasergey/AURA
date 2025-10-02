package com.shulha.betapsycho.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import jakarta.annotation.PostConstruct;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Slf4j
@Component
public class JwtTokenProvider {

    // ==== SET PIN ONLY TOKEN ====
    @Getter
    private Key pinKey;
    @Value("${jwt.pin.secret}")
    private String pinSecretStr;
    @Value("${jwt.pin.ttlMs}")
    private long pinTtlMs;

    // ==== ACCESS TOKEN ====
    private Key accessKey;
    @Value("${jwt.access.secret}")
    private String accessSecretStr;
    @Value("${jwt.access.ttlMs}")
    private long accessTtlMs;

    // ==== REGISTRATION ONLY TOKEN ====
    private Key registrationKey;
    @Value("${jwt.registration.secret}")
    private String registrationSecretStr;
    @Value("${jwt.registration.ttlMs}")
    private long registrationTtlMs;

    // ==== REFRESH TOKEN ====
    @Value("${jwt.refresh.secret}")
    private String refreshSecretStr;
    @Value("${jwt.refresh.ttlMs}")
    private long refreshTtlMs;
    private Key refreshKey;

    @Value("${jwt.clockSkewSeconds:30}")
    private long clockSkewSeconds;

    @PostConstruct
    void init() {
        this.pinKey = Keys.hmacShaKeyFor(decode(pinSecretStr));
        this.accessKey = Keys.hmacShaKeyFor(decode(accessSecretStr));
        this.refreshKey = Keys.hmacShaKeyFor(decode(refreshSecretStr));
        this.registrationKey = Keys.hmacShaKeyFor(decode(registrationSecretStr));

        if (accessKey.getEncoded().length < 32
                || registrationKey.getEncoded().length < 32
                || pinKey.getEncoded().length < 32) {
            throw new IllegalStateException("JWT keys must be >= 256-bit for HS256.");
        }
    }

    private static byte[] decode(String v) {
        try {
            return Decoders.BASE64.decode(v);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException occurred in JwtTokenProvider.decode()", e);
            return v.getBytes();
        }
    }

    // ==== ACCESS TOKEN ====
    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTtlMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("typ", "ACCESS")
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims c = Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .setAllowedClockSkewSeconds(clockSkewSeconds)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return "ACCESS".equals(c.get("typ"));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JwtException | IllegalArgumentException occurred in JwtTokenProvider.validateAccessToken()", e);
            return false;
        }
    }

    public Long getUserIdFromAccessToken(String token) {
        Claims c = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(c.getSubject());
    }

    public Long getCurrentUserIdFromAccessToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authentication found in context");
        }
        return (Long) auth.getPrincipal();
    }

    // ==== REGISTRATION TOKEN ====
    public String generateRegistrationToken(String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + registrationTtlMs);
        return Jwts.builder()
                // .setSubject(...)
                .claim("typ", "REGISTRATION")
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(registrationKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateRegistrationToken(String token) {
        try {
            Claims c = Jwts.parserBuilder()
                    .setSigningKey(registrationKey)
                    .setAllowedClockSkewSeconds(clockSkewSeconds)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return "REGISTRATION".equals(c.get("typ"));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JwtException | IllegalArgumentException occurred in JwtTokenProvider.validateRegistrationToken()", e);
            return false;
        }
    }

    public String getEmailFromRegistrationToken(String token) {
        Claims c = Jwts.parserBuilder()
                .setSigningKey(registrationKey)
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) c.get("email");
    }

    // ==== PIN TOKEN ====
    public String generatePinToken(Long userId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + pinTtlMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("typ", "PIN")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(pinKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validatePinToken(String token) {
        try {
            Claims c = Jwts.parserBuilder()
                    .setSigningKey(pinKey)
                    .setAllowedClockSkewSeconds(clockSkewSeconds)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return "PIN".equals(c.get("typ"));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JwtException | IllegalArgumentException occurred in JwtTokenProvider.validatePinToken()", e);
            return false;
        }
    }

    public Long getUserIdFromPinToken(String token) {
        Claims c = Jwts.parserBuilder()
                .setSigningKey(pinKey)
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(c.getSubject());
    }

    // ==== REFRESH TOKEN ====

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTtlMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("typ", "REFRESH")
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims c = Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .setAllowedClockSkewSeconds(clockSkewSeconds)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return "REFRESH".equals(c.get("typ"));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JwtException | IllegalArgumentException occurred in JwtTokenProvider.validateRefreshToken()", e);
            return false;
        }
    }

    public Long getUserIdFromRefreshToken(String token) {
        Claims c = Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(c.getSubject());
    }

    public Long getCurrentUserIdFromRefreshToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authentication found in context");
        }
        return (Long) auth.getPrincipal();
    }

}