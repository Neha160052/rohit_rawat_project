package org.project.ttnecommerce.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.LoginRequest;
import org.project.ttnecommerce.dto.LoginResponse;
import org.project.ttnecommerce.entity.RefreshToken;
import org.project.ttnecommerce.entity.User;
import org.project.ttnecommerce.exception.AccountLockedException;
import org.project.ttnecommerce.exception.InvalidCredentialsException;
import org.project.ttnecommerce.exception.UserNotFoundException;
import org.project.ttnecommerce.repository.RefreshTokenRepository;
import org.project.ttnecommerce.repository.UserRepository;
import org.project.ttnecommerce.security.CustomUserDetails;
import org.project.ttnecommerce.security.Utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.beans.Transient;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final LoginAttemptService loginAttemptService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    public LoginResponse login(LoginRequest request,String role) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if(user.getIsDeleted())
            throw new RuntimeException("User deleted");

        if(!user.getIsActive())
            throw new RuntimeException("Account not activated");

        if(user.getIsLocked())
            throw new AccountLockedException("Account locked");

        if(user.getIsExpired())
            throw new RuntimeException("Account expired");

        boolean hasRole = user.getUserRoles()
                .stream()
                .anyMatch(r -> r.getRole().getAuthority().equals(role));

        if(!hasRole)
            throw new RuntimeException("Invalid login endpoint for user role");

        try{

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );

            CustomUserDetails userDetails =
                    (CustomUserDetails) authentication.getPrincipal();

            loginAttemptService.loginSucceeded(user);

            String accessToken = jwtUtils.generateToken(userDetails);

            RefreshToken refreshToken =
                    refreshTokenService.createRefreshToken(user);

            return new LoginResponse(accessToken,refreshToken.getToken());

        }catch(BadCredentialsException e){

            loginAttemptService.loginFailed(user);

            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    @Transactional
    public void logout(String accessToken){

        String email = jwtUtils.extractUsername(accessToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        refreshTokenRepository.deleteByUser(user);
    }
}