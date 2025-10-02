package com.shulha.betapsycho.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private List<String> openPaths;
    private List<String> registrationRequired;
    private List<String> pinRequired;
    private List<String> refreshRequired;
}