package com.cenimarket.backend.auth.repository;

import com.cenimarket.backend.auth.domain.EmailVerification;
import com.cenimarket.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {


    Optional<EmailVerification> findTopByEmailOrderByCreatedAtDesc(String email);
}

