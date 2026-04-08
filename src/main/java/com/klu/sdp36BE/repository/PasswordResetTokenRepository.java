package com.klu.sdp36BE.repository;

import com.klu.sdp36BE.model.PasswordResetToken;
import com.klu.sdp36BE.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
    void deleteByToken(String token);
}
