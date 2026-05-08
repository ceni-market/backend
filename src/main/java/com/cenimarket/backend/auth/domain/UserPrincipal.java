package com.cenimarket.backend.auth.domain;


import com.cenimarket.backend.user.domain.User; // 유저 엔티티 임포트
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
public class UserPrincipal implements UserDetails {

    private final User user; // 우리 프로젝트의 User 엔티티

    public UserPrincipal(User user) {
        this.user = user;
    }

    // 이 메서드가 있어야 컨트롤러에서 .get***()을 쓸 수 있습니다!
    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getName() {
        return user.getName();
    }

    public String getProfileImageUrl() {
        return user.getProfileImageUrl();
    }

    public String getStatus() {
        return user.getStatus() != null ? user.getStatus().name() : null; // 예: ACTIVE, PENDING 등
    }

    public LocalDateTime getEmailVerifiedAt() {
        return user.getEmailVerifiedAt();
    }
    @Override
    public String getUsername() {
        return user.getEmail(); // 이메일을 로그인 ID로 사용
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 설정 (일단 빈 리스트 혹은 기본 권한)
        return Collections.emptyList();
    }

    // 계정 상태 관리 (모두 true로 설정해야 로그인이 됩니다)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}