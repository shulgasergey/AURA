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

public enum UserPersonalInterest {
    BOOKS("Books"),
    MOVIES("Movies"),
    HIKING("Hiking"),
    SELF_EDUCATION("Self educations");

    private final String personalInterest;

    UserPersonalInterest(String personalInterest) {
        this.personalInterest = personalInterest;
    }

    public String get() {
        return personalInterest;
    }
}