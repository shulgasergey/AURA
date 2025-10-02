package com.shulha.betapsycho.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

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

@Data
@AllArgsConstructor
public class OtpStorageDto {
    private String otpCode;
    private Short failedAttempts;
    private LocalDateTime createdAt;
    private LocalDateTime blockedUntil;
    private LocalDateTime sendingOtpForbiddenUntil;
}