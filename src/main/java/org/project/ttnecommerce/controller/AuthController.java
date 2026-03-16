package org.project.ttnecommerce.controller;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {

        LoginResponse loginResponse = authService.login(request, response);

        return ResponseEntity.ok(loginResponse);
    }

    // REFRESH ACCESS TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidToken("Invalid refresh token"));

        refreshTokenService.verifyExpiration(token);

        String accessToken =
                jwtUtils.generateToken(
                        new CustomUserDetails(token.getUser())
                );

        return ResponseEntity.ok(new LoginResponse(accessToken));
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestHeader("Authorization") String authHeader) {

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

                    response.addHeader(
                            HttpHeaders.SET_COOKIE,
                            deleteCookie.toString()
                    );
                }
            }
        }

        return ResponseEntity.ok("Logout successful");
    }
}