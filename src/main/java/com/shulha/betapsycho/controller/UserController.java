package com.shulha.betapsycho.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.shulha.betapsycho.security.JwtTokenProvider;
import com.shulha.betapsycho.dto.user.UserPreferences;
import com.shulha.betapsycho.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

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
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PatchMapping("/preferences")
    public ResponseEntity<Map<String, String>> changeUserPreferences(@RequestBody UserPreferences userPreferences) {
        log.info("New request to change user preferences");
        Long currentUserId = jwtTokenProvider.getCurrentUserIdFromAccessToken();
        userService.changeUserPreferences(currentUserId, userPreferences);
        log.info("User preferences changed successfully");
        return ResponseEntity.ok(createResponse("User preferences changed successfully"));
    }

    private Map<String, String> createResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("Response", message);
        return response;
    }

}