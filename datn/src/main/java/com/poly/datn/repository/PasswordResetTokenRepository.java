package com.poly.datn.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByToken(String token);
}
