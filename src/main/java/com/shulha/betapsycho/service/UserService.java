package com.shulha.betapsycho.service;

import com.shulha.betapsycho.dto.auth.LoginRequestDto;
import com.shulha.betapsycho.dto.auth.PinRequestDto;
import com.shulha.betapsycho.dto.auth.RegistrationRequestDto;
import com.shulha.betapsycho.model.User;
import com.shulha.betapsycho.repository.UserRepository;
import com.shulha.betapsycho.security.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String registerUser(RegistrationRequestDto registrationRequestDto) {

        if (userRepository.findByEmail(registrationRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exist");
        }

        String hashedPassword = passwordEncoder.encode(registrationRequestDto.getPassword());
        User user = new User();
        user.setEmail(registrationRequestDto.getEmail());
        user.setPassword(hashedPassword);
        user.setDeviceId(registrationRequestDto.getDeviceId());
        User savedUser = userRepository.save(user);

        return jwtTokenProvider.generatePinToken(savedUser.getId());
    }

    public Map<String, String> loginUser(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with this email doesn't exist"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    public Map<String, String> setUpPin(PinRequestDto pinRequestDto) {
        User user = userRepository.findByEmail(pinRequestDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User with this email doesn't exist"));

        String hashedPin = passwordEncoder.encode(pinRequestDto.getPin());
        user.setPin(hashedPin);
        userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    public Map<String, String> verifyPin(PinRequestDto pinRequestDto) {
        User user = userRepository.findByEmail(pinRequestDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User with this email doesn't exist"));

        if (passwordEncoder.matches(pinRequestDto.getPin(), user.getPin())) {
            String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        } else {
            throw new IllegalArgumentException("Invalid PIN");
        }
    }
}