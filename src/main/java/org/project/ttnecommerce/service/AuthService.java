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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        log.info("Login attempt for email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (user.getIsLocked()) {
            throw new AccountLockedException("Account locked");
        }

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }
        catch (DisabledException ex) {
            throw new AccountNotActivatedException("Account is not activated");
        }
        catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        String accessToken = jwtUtils.generateToken(userDetails);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.info("User logged in successfully: {}", user.getEmail());
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
                User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

                user.setTokenVersion(user.getTokenVersion() + 1);
                userRepository.save(user);
                log.info("Access tokens invalidated for user: {}", email);

            }
            catch (Exception e) {
                log.warn("Invalid token during logout");
            }
        }
        log.info("User logged out successfully");
    }
}