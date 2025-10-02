package com.shulha.betapsycho.dto.auth;

import lombok.Data;

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
public class RegistrationRequestDto {
    private String email;
    private String password;
    private String deviceId;
}