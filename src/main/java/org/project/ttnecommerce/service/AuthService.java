package org.project.ttnecommerce.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.LoginRequest;
import org.project.ttnecommerce.dto.LoginResponse;
import org.project.ttnecommerce.entity.BlacklistedToken;
import org.project.ttnecommerce.entity.RefreshToken;
import org.project.ttnecommerce.entity.User;
import org.project.ttnecommerce.exception.AccountLockedException;
import org.project.ttnecommerce.exception.UserNotFoundException;
import org.project.ttnecommerce.repository.BlacklistedTokenRepository;
import org.project.ttnecommerce.repository.RefreshTokenRepository;
import org.project.ttnecommerce.repository.UserRepository;
import org.project.ttnecommerce.security.CustomUserDetails;
import org.project.ttnecommerce.security.Utils.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public LoginResponse login(LoginRequest request,
                               HttpServletResponse response) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getIsLocked())
            throw new AccountLockedException("Account locked");

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String accessToken;
        RefreshToken refreshToken;

        Optional<RefreshToken> existingToken =
                refreshTokenRepository.findByUser_Id(user.getId());

        if (existingToken.isPresent() &&
                existingToken.get().getExpiryDate()
                        .isAfter(LocalDateTime.now())) {

            refreshToken = existingToken.get();

        } else {

            refreshTokenRepository.deleteByUser_Id(user.getId());

            refreshToken = refreshTokenService.createRefreshToken(user);
        }

        accessToken = jwtUtils.generateToken(userDetails);

        ResponseCookie cookie =
                ResponseCookie.from("refreshToken", refreshToken.getToken())
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(24 * 60 * 60)
                        .sameSite("Strict")
                        .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new LoginResponse(accessToken);
    }

    @Transactional
    public void logout(String refreshToken, String accessToken) {

        RefreshToken token =
                refreshTokenRepository.findByToken(refreshToken)
                        .orElseThrow(() ->
                                new RuntimeException("Token not found"));

        refreshTokenRepository.delete(token);

        BlacklistedToken blacklisted = new BlacklistedToken();

        blacklisted.setToken(accessToken);
        blacklisted.setExpiryDate(
                jwtUtils.extractExpiration(accessToken)
                        .toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        blacklistedTokenRepository.save(blacklisted);
    }
}