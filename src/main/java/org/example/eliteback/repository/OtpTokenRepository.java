package org.example.eliteback.repository;

import org.example.eliteback.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    List<OtpToken> findByEmailOrderByCreatedAtDesc(String email);
    void deleteByExpiresAtBefore(Instant instant);
}
