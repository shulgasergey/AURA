package com.shulha.betapsycho.enums;

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

public enum ModelAnswerType {
    OBJECTIVE("Objective"),
    SUBJECTIVE("Subjective");

    private final String answerType;

    ModelAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public String get() {
        return answerType;
    }
}