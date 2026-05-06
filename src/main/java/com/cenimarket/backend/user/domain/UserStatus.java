package com.cenimarket.backend.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    // 이미지 4645645666.PNG의 '설명' 및 '비고' 컬럼 내용을 반영
    ACTIVE("활성화"),
    INACTIVE("비활성화"),// 기본값: ACTIVE
    SUSPENDED("정지"),   // 계정 이용 제한 상태
    DELETED("해지");   // 계정 해지 상태

    private final String description;
}