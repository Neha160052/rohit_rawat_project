package org.project.ttnecommerce.service;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.ttnecommerce.dto.LoginRequest;
import org.project.ttnecommerce.dto.LoginResponse;
import org.project.ttnecommerce.entity.RefreshToken;
import org.project.ttnecommerce.entity.User;
import org.project.ttnecommerce.exception.AccountLockedException;
import org.project.ttnecommerce.exception.AccountNotActivatedException;
import org.project.ttnecommerce.exception.InvalidCredentialsException;
import org.project.ttnecommerce.exception.UserNotFoundException;
import org.project.ttnecommerce.repository.RefreshTokenRepository;
import org.project.ttnecommerce.repository.UserRepository;
import org.project.ttnecommerce.security.CustomUserDetails;
import org.project.ttnecommerce.security.Utils.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final LoginAttemptService loginAttemptService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {

        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (user.getIsLocked()) {
            log.error("LOGIN BLOCKED - ACCOUNT LOCKED: {}", user.getEmail());
            throw new AccountLockedException("Account locked");
        }

        if (!user.getIsActive()) {
            throw new AccountNotActivatedException("Account is not activated");
        }

        boolean isPasswordCorrect = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!isPasswordCorrect) {
            loginAttemptService.loginFailed(user);
            log.error("FAILED LOGIN: {}", user.getEmail());

            throw new InvalidCredentialsException("Invalid email or password");
        }

        log.info("LOGIN SUCCESS: {}", user.getEmail());
        loginAttemptService.loginSucceeded(user);
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String accessToken = jwtUtils.generateToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

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

        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(refreshTokenRepository::delete);
        }

        if (accessToken != null && !accessToken.isBlank()) {
            try {
                String email = jwtUtils.extractUsername(accessToken);

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

                user.setTokenVersion(user.getTokenVersion() + 1);
                userRepository.save(user);

                log.info("Tokens invalidated for user: {}", email);

            } catch (Exception e) {
                log.warn("Invalid token during logout");
            }
        }

        log.info("Logout completed");
    }
}