package org.project.ttnecommerce.repository;

import org.project.ttnecommerce.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    // find token using the refresh token string
    Optional<RefreshToken> findByToken(String token);

    // find refresh token by user id (property path: user.id)
    Optional<RefreshToken> findByUser_Id(UUID userId);

    // delete existing refresh token of a user
    void deleteByUser_Id(UUID userId);
}