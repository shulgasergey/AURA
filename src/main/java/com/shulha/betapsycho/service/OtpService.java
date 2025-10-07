package com.shulha.betapsycho.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.mail.MessagingException;
import java.util.concurrent.ConcurrentHashMap;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.io.IOException;
import java.util.Optional;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shulha.betapsycho.exception.customException.TooManyRequestsException;
import com.shulha.betapsycho.security.JwtTokenProvider;
import com.shulha.betapsycho.dto.auth.OtpStorageDto;

import lombok.RequiredArgsConstructor;

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
public class OtpService {
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.otp.maximum.failed.attempts}")
    private int MAXIMUM_FAILED_ATTEMPTS;

    @Value("${app.otp.delay.minutes}")
    private int DELAY_MINUTES_BETWEEN_NEW_OTP_REQUEST;

    private final Map<String, OtpStorageDto> otpStorage = new ConcurrentHashMap<>();

    public void generateAndSendOtpCode(String emailDestination) throws MessagingException, IOException, IllegalAccessException {

        String normalizedEmail = normalizeEmail(emailDestination);

        if (isOtpBlocked(normalizedEmail))
            throw new IllegalAccessException("Email destination blocked after 10 failed attempts. Try again in one day.");

        if (!isOtpAccessible(normalizedEmail))
            throw new TooManyRequestsException("Too many requests. Try again later.");

        String otpCode = generateOtp();

        otpStorage.put(
                normalizedEmail,
                new OtpStorageDto(otpCode, (short) 0, LocalDateTime.now(), null, LocalDateTime.now().plusMinutes(DELAY_MINUTES_BETWEEN_NEW_OTP_REQUEST))
        );

        emailService.sendEmailOtp(normalizedEmail, otpCode);
    }

    public String validateOtp(String emailDestination, String code) throws IllegalAccessException {

        String normalizedEmail = normalizeEmail(emailDestination);

        if (!isEmailDestinationExistInStorage(normalizedEmail))
            throw new EntityNotFoundException("Email destination didn't exist in OTP storage");

        if (isOtpBlocked(normalizedEmail))
            throw new IllegalAccessException("Email destination blocked after 10 failed attempts. Try again in one day.");

        if (otpStorage.get(normalizedEmail).getOtpCode().equals(code)) {
            otpStorage.remove(normalizedEmail);
            return jwtTokenProvider.generateRegistrationToken(emailDestination);
        } else {
            short currentFailedAttempts = otpStorage.get(normalizedEmail).getFailedAttempts();
            otpStorage.get(normalizedEmail).setFailedAttempts((short) (currentFailedAttempts + 1));
            otpStorage.get(normalizedEmail).setSendingOtpForbiddenUntil(LocalDateTime.now().plusMinutes(DELAY_MINUTES_BETWEEN_NEW_OTP_REQUEST));

            if (otpStorage.get(normalizedEmail).getFailedAttempts() >= MAXIMUM_FAILED_ATTEMPTS) {
                otpStorage.get(normalizedEmail).setBlockedUntil(LocalDateTime.now().plusDays(1));
            }

            throw new AccessDeniedException("Incorrect OTP");
        }
    }

    private String generateOtp() {
        return String.format("%04d", secureRandom.nextInt(10_000));
    }

    private boolean isOtpAccessible(String emailDestination) {
        if (isEmailDestinationExistInStorage(emailDestination)) {
            return otpStorage.get(emailDestination).getSendingOtpForbiddenUntil().isBefore(LocalDateTime.now());
        } else {
            return true;
        }
    }

    private boolean isOtpBlocked(String emailDestination) {
        if (isEmailDestinationExistInStorage(emailDestination)) {
            return (otpStorage.get(emailDestination).getFailedAttempts() >= MAXIMUM_FAILED_ATTEMPTS
            && otpStorage.get(emailDestination).getBlockedUntil() != null
            && otpStorage.get(emailDestination).getBlockedUntil().isAfter(LocalDateTime.now()));
        } else {
            return false;
        }
    }

    private boolean isEmailDestinationExistInStorage(String emailDestination) {
        return otpStorage.get(emailDestination) != null;
    }

    private String normalizeEmail(String emailDestination) {
        return emailDestination.trim().toLowerCase();
    }

    @Scheduled(fixedDelay = 300000)
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();

        otpStorage.entrySet().removeIf(entry ->
                entry.getValue().getCreatedAt().plusMinutes(DELAY_MINUTES_BETWEEN_NEW_OTP_REQUEST).isBefore(now)
        );
    }

    @Scheduled(fixedDelay = 86400000)
    public void cleanupBlockedEmailDestinations() {
        LocalDateTime now = LocalDateTime.now();

        otpStorage.entrySet().removeIf(e ->
                Optional.ofNullable(e.getValue().getBlockedUntil())
                        .map(b -> b.isBefore(now))
                        .orElse(false)
        );
    }
}