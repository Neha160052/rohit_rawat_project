package org.project.ttnecommerce.repository;

import org.project.ttnecommerce.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser_Id(UUID userId);

    // used to delete existing token when new login happens
    void deleteByUser_Id(UUID userId);
}