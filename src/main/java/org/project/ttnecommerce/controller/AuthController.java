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

    @PostMapping("/customer/login")
    public ResponseEntity<LoginResponse> customerLogin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request,"CUSTOMER"));
    }

    @PostMapping("/seller/login")
    public ResponseEntity<LoginResponse> sellerLogin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request,"SELLER"));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> adminLogin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request,"ADMIN"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshRequest request) {

        RefreshToken token = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidToken("Invalid refresh token"));

        refreshTokenService.verifyExpiration(token);

        String accessToken =
                jwtUtils.generateToken(new CustomUserDetails(token.getUser()));

        return ResponseEntity.ok(
                new LoginResponse(accessToken, token.getToken())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Authorization header missing");

        String token = authHeader.substring(7);

        authService.logout(token);

        return ResponseEntity.ok("Logout successful");
    }
}