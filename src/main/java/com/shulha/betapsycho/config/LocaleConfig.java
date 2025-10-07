package com.shulha.betapsycho.config;

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

import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    @Bean
    public FixedLocaleResolver localeResolver() {
        return new FixedLocaleResolver(Locale.ENGLISH);
    }
}