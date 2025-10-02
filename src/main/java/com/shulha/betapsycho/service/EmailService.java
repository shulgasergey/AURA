package com.shulha.betapsycho.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.nio.file.Files;

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
@Service
public class EmailService {
    @Value("${app.otp.from.name}")
    private String NAME_SENDER;
    @Value("${app.otp.from}")
    private String EMAIL_SENDER;
    @Value("${app.otp.subject}")
    private String VERIFICATION_SUBJECT;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailOtp(String toEmail, String code) throws MessagingException, IOException {

        String html = Files.readString(new ClassPathResource("templates/otp-template.html").getFile().toPath());
        html = html.replace("{{CODE}}", code);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String fromEmail = EMAIL_SENDER;
        String fromName = NAME_SENDER;
        helper.setFrom(fromEmail, fromName);
        helper.setTo(toEmail);
        helper.setSubject(VERIFICATION_SUBJECT);
        helper.setText(html, true);

        mailSender.send(message);

        log.info("Email with OTP successfully sent");
    }
}