package com.shulha.betapsycho.controller;

import com.shulha.betapsycho.dto.auth.RegistrationRequestDto;
import com.shulha.betapsycho.dto.auth.LoginRequestDto;
import com.shulha.betapsycho.dto.auth.PinRequestDto;
import com.shulha.betapsycho.service.UserService;
import com.shulha.betapsycho.service.AuthService;
import com.shulha.betapsycho.service.OtpService;

import jakarta.validation.constraints.Email;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtpToEmail(@RequestParam @Email String emailDestination) throws MessagingException, IOException, IllegalAccessException {
        log.info("New request to send OTP to email {}", emailDestination);
        otpService.generateAndSendOtpCode(emailDestination);
        log.info("OTP successfully generated and sent to email {}", emailDestination);
        return ResponseEntity.ok(createResponse("OTP successfully generated and sent to email"));
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<Map<String, String>> validateOtp(@RequestParam @Email String emailDestination,
                                                           @RequestParam String code) throws IllegalAccessException {
        log.info("New request to validate OTP for {}", emailDestination);
        String registrationToken = otpService.validateOtp(emailDestination, code);
        log.info("OTP is valid for {}", emailDestination);
        return ResponseEntity.ok(createResponse(registrationToken));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegistrationRequestDto registrationRequestDto) {
        log.info("New request to register user");
        String pinToken = userService.registerUser(registrationRequestDto);
        log.info("User registered successfully");
        return ResponseEntity.ok(createResponse(pinToken));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDto loginRequestDto) {
        log.info("New request to login");
        Map<String, String> response = userService.loginUser(loginRequestDto);
        log.info("User successfully logged in");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pin/set")
    public ResponseEntity<Map<String, String>> setPin(@RequestBody PinRequestDto pinRequestDto) {
        log.info("New request to set PIN");
        Map<String, String> response = userService.setUpPin(pinRequestDto);
        log.info("PIN successfully was set up");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pin/verify")
    public ResponseEntity<Map<String, String>> verifyPin(@RequestBody PinRequestDto pinRequestDto) {
        log.info("New info to login via PIN");
        Map<String, String> response = userService.verifyPin(pinRequestDto);
        log.info("Login via PIN processed successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refresh() {
        log.info("New request to refresh token");
        Map<String, String> response = authService.refreshTokens();
        log.info("Tokens refreshed successfully");
        return ResponseEntity.ok(response);
    }

    private Map<String, String> createResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("Response", message);
        return response;
    }
}