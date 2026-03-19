package org.project.ttnecommerce.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> {
                    log.warn("Login failed - user not found: {}", request.getEmail());
                    return new UserNotFoundException("User not found");
                });

        if (user.getIsLocked()) {
            log.warn("Login failed - account locked for email: {}", request.getEmail());
            throw new AccountLockedException("Account locked");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        log.info("Authentication successful for email: {}", request.getEmail());

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String accessToken;
        RefreshToken refreshToken;

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser_Id(user.getId());
        if (existingToken.isPresent() &&
                existingToken.get().getExpiryDate()
                        .isAfter(LocalDateTime.now())) {

            log.debug("Using existing refresh token for user: {}", request.getEmail());
            refreshToken = existingToken.get();

        }
        else {
            log.debug("Generating new refresh token for user: {}", request.getEmail());
            refreshTokenRepository.deleteByUser_Id(user.getId());
            refreshToken = refreshTokenService.createRefreshToken(user);
        }

        accessToken = jwtUtils.generateToken(userDetails);
        log.info("Access token generated for user: {}", request.getEmail());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
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
        log.info("Logout request received");
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                        .orElseThrow(() -> {
                            log.warn("Logout failed - refresh token not found");
                            return new RuntimeException("Token not found");
                        });

        refreshTokenRepository.delete(token);
        log.info("Refresh token deleted successfully");
        BlacklistedToken blacklisted = new BlacklistedToken();
        blacklisted.setToken(accessToken);
        blacklisted.setExpiryDate(jwtUtils.extractExpiration(accessToken)
                        .toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        blacklistedTokenRepository.save(blacklisted);
        log.info("Access token blacklisted successfully");
    }
}