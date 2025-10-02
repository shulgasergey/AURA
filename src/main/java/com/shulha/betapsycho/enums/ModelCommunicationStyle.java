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

public enum ModelCommunicationStyle {
    FRIENDLY("Friendly"),
    OFFICIAL("Official");

    private final String communicationStyle;

    ModelCommunicationStyle(String communicationStyle) {
        this.communicationStyle = communicationStyle;
    }

    public String get() {
        return communicationStyle;
    }
}