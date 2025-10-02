package com.shulha.betapsycho.security;

/**
 * This source code and all associated intellectual property
 * are the exclusive property of the author.
 * --------------------------------------------------
 * Unauthorized copying, modification, distribution, or derivative
 * works without prior written consent is strictly prohibited
 * and may be prosecuted to the fullest extent of the law.
 * --------------------------------------------------
 * Copyright © 2025 BetaPsycho Serhii Shulha.
 */

import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinTokenService {

    private final JwtTokenProvider jwtTokenProvider;

    private final Map<String, Instant> usedTokens = new ConcurrentHashMap<>();

    public boolean validateAndConsume(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtTokenProvider.getPinKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String typ = (String) claims.get("typ");
            if (!"PIN".equals(typ)) return false;

            String jti = claims.getId();
            Instant exp = claims.getExpiration().toInstant();

            if (usedTokens.containsKey(jti)) return false;
            if (exp.isBefore(Instant.now())) return false;

            usedTokens.put(jti, Instant.now());
            return true;

        } catch (Exception e) {
            log.error("Exception occurred in PinTokenService.validateAndConsume()", e);
            return false;
        }
    }
}