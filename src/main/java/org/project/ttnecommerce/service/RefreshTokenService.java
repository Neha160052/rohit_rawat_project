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
    public RefreshToken createRefreshToken(User user) {

        /*
         IMPORTANT FIX

         jab bhi new login hota hai
         us user ka existing refresh token delete kar dete hain

         isse DB me ek user ka sirf ek refresh token rahega
        */
        refreshTokenRepository.deleteByUser_Id(user.getId());

        // create new refresh token
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token expired");
        }
        return token;
    }
}