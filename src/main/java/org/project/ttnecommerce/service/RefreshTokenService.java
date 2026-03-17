package org.project.ttnecommerce.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.entity.RefreshToken;
import org.project.ttnecommerce.entity.User;
import org.project.ttnecommerce.exception.TokenRefreshException;
import org.project.ttnecommerce.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(User user){

        refreshTokenRepository.findByUserId(user.getId())
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken token = new RefreshToken();

        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusHours(24));

        return refreshTokenRepository.save(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){

        if(token.getExpiryDate().isBefore(LocalDateTime.now())){

            refreshTokenRepository.delete(token);

            throw new TokenRefreshException("Refresh token expired");
        }

        return token;
    }
}