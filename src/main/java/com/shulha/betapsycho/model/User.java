package com.shulha.betapsycho.model;

import com.shulha.betapsycho.enums.ModelCommunicationStyle;
import com.shulha.betapsycho.enums.UserPersonalInterest;
import com.shulha.betapsycho.enums.ModelAnswerType;

import jakarta.persistence.*;
import java.util.List;

import jakarta.validation.constraints.NotNull;
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
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String email;
    @NotNull
    private String password;
    private String pin;
    private String deviceId;

    @Enumerated(EnumType.STRING)
    private ModelAnswerType modelAnswerType;

    @Enumerated(EnumType.STRING)
    private ModelCommunicationStyle modelCommunicationStyle;

    @ElementCollection(targetClass = UserPersonalInterest.class)
    @CollectionTable(
            name = "user_personal_interest",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "user_personal_interest")
    @Enumerated(EnumType.STRING)
    private List<UserPersonalInterest> userPersonalInterests;
}