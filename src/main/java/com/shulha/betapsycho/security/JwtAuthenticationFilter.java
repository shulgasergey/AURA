package com.shulha.betapsycho.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.lang.NonNull;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.FilterChain;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.king.token}")
    private String hardcodedToken;
    private final JwtTokenProvider jwtTokenProvider;
    private final PinTokenService pinTokenService;
    private final SecurityProperties securityProperties;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   PinTokenService pinTokenService,
                                   SecurityProperties securityProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.pinTokenService = pinTokenService;
        this.securityProperties = securityProperties;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest req) {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true;
        String uri = req.getRequestURI();
        return securityProperties.getOpenPaths().stream().anyMatch(p -> matcher.match(p, uri));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {

        String token = resolveToken(request);
        String uri = request.getRequestURI();

        if (hardcodedToken.equals(token)) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(1L, null, List.of()));
            chain.doFilter(request, response);
            return;
        }

        if (!StringUtils.hasText(token)) {
            chain.doFilter(request, response);
            return;
        }

        // Registration token
        if (securityProperties.getRegistrationRequired().stream().anyMatch(p -> matcher.match(p, uri))) {
            if (!jwtTokenProvider.validateRegistrationToken(token)) {
                chain.doFilter(request, response);
                return;
            }
            String email = jwtTokenProvider.getEmailFromRegistrationToken(token);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(email, null, List.of()));
            chain.doFilter(request, response);
            return;
        }

        // Pin token
        if (securityProperties.getPinRequired().stream().anyMatch(p -> matcher.match(p, uri))) {
            if (!pinTokenService.validateAndConsume(token)) {
                chain.doFilter(request, response);
                return;
            }
            Long userId = jwtTokenProvider.getUserIdFromPinToken(token);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userId, null, List.of()));
            chain.doFilter(request, response);
            return;
        }

        // Refresh token
        if (securityProperties.getRefreshRequired().stream().anyMatch(p -> matcher.match(p, uri))) {
            if (!jwtTokenProvider.validateRefreshToken(token)) {
                chain.doFilter(request, response);
                return;
            }
            Long userId = jwtTokenProvider.getUserIdFromRefreshToken(token);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userId, null, List.of()));
            chain.doFilter(request, response);
            return;
        }

        // Access token
        if (!jwtTokenProvider.validateAccessToken(token)) {
            chain.doFilter(request, response);
            return;
        }

        Long userId = jwtTokenProvider.getUserIdFromAccessToken(token);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, List.of()));

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (StringUtils.hasText(bearer) && bearer.startsWith("Bearer "))
                ? bearer.substring(7)
                : null;
    }

}