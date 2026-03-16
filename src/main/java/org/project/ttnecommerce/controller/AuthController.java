package org.project.ttnecommerce.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.ttnecommerce.dto.LoginRequest;
import org.project.ttnecommerce.dto.LoginResponse;
import org.project.ttnecommerce.entity.RefreshToken;
import org.project.ttnecommerce.exception.InvalidToken;
import org.project.ttnecommerce.security.CustomUserDetails;
import org.project.ttnecommerce.security.Utils.JwtUtils;
import org.project.ttnecommerce.service.AuthService;
import org.project.ttnecommerce.service.RefreshTokenService;
import org.project.ttnecommerce.repository.RefreshTokenRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        log.info("Login API called for email: {}", request.getEmail());
        LoginResponse loginResponse = authService.login(request, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken) {
        log.info("Refresh token API called");
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("Invalid refresh token used");
                    return new InvalidToken("Invalid refresh token");
                });
        refreshTokenService.verifyExpiration(token);

        String accessToken = jwtUtils.generateToken(new CustomUserDetails(token.getUser()));

        log.info("New access token generated for user: {}", token.getUser().getEmail());
        return ResponseEntity.ok(new LoginResponse(accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, @RequestHeader("Authorization") String authHeader) {
        log.info("Logout API called");
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();
                    authService.logout(refreshToken, accessToken);
                    ResponseCookie deleteCookie =
                            ResponseCookie.from("refreshToken", "")
                                    .httpOnly(true)
                                    .secure(false)
                                    .path("/")
                                    .maxAge(0)
                                    .sameSite("Strict")
                                    .build();

                    response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
                    log.info("User logged out successfully");
                }
            }
        }
        return ResponseEntity.ok("Logout successful");
    }
}