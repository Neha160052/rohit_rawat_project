package org.project.ttnecommerce.repository;
import org.project.ttnecommerce.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, UUID> {

    boolean existsByToken(String token);

    void deleteByExpiryDateBefore(LocalDateTime time);
}