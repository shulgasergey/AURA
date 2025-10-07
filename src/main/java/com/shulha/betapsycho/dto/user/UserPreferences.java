package com.shulha.betapsycho.dto.user;

import com.shulha.betapsycho.enums.ModelCommunicationStyle;
import com.shulha.betapsycho.enums.UserPersonalInterest;
import com.shulha.betapsycho.enums.ModelAnswerType;

import java.util.List;
import lombok.Getter;

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
public class UserPreferences {
    private ModelAnswerType modelAnswerType;
    private ModelCommunicationStyle modelCommunicationStyle;
    private List<UserPersonalInterest> userPersonalInterests;
}