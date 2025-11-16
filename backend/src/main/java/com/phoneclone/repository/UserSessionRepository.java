package com.phoneclone.repository;

import com.phoneclone.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByToken(String token);
    void deleteByUserId(Long userId);
    void deleteByExpiresAtBefore(java.time.LocalDateTime dateTime);
}

