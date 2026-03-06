package org.project.ttnecommerce.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.LoginRequest;
import org.project.ttnecommerce.dto.LoginResponse;
import org.project.ttnecommerce.dto.RefreshRequest;
import org.project.ttnecommerce.entity.RefreshToken;
import org.project.ttnecommerce.exception.InvalidToken;
import org.project.ttnecommerce.security.CustomUserDetails;
import org.project.ttnecommerce.security.Utils.JwtUtils;
import org.project.ttnecommerce.service.AuthService;
import org.project.ttnecommerce.service.RefreshTokenService;
import org.project.ttnecommerce.repository.RefreshTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshRequest request) {

        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken()).orElseThrow(() -> new InvalidToken("Invalid refresh token"));
        refreshTokenService.verifyExpiration(token);
        String newAccessToken = jwtUtils.generateToken(new CustomUserDetails(token.getUser()));
        return ResponseEntity.ok(new LoginResponse(newAccessToken, token.getToken()));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization token missing");
        }

        String accessToken = authHeader.substring(7);
        authService.logout(accessToken);
        return ResponseEntity.ok("Logout successful");
    }
}