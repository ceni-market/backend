package com.cenimarket.backend.auth.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VerificationPurpose {
    SIGN_UP("가입"),
    PASSWORD_RESET("비밀번호변경");

    private final String description;
}
