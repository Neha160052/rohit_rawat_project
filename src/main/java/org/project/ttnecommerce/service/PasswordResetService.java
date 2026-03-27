package org.project.ttnecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.ttnecommerce.dto.ForgotPasswordRequest;
import org.project.ttnecommerce.dto.ResetPasswordRequest;
import org.project.ttnecommerce.entity.ActivationToken;
import org.project.ttnecommerce.entity.User;
import org.project.ttnecommerce.exception.AccountNotActivatedException;
import org.project.ttnecommerce.exception.InvalidToken;
import org.project.ttnecommerce.exception.PasswordMismatchException;
import org.project.ttnecommerce.exception.UserNotFoundException;
import org.project.ttnecommerce.repository.ActivationTokenRepository;
import org.project.ttnecommerce.repository.UserRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getIsActive()) {
            throw new AccountNotActivatedException("Account not activated");
        }
        activationTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setUser(user);
        activationToken.setExpiryDate(LocalDateTime.now().plusMinutes(2));

        activationTokenRepository.save(activationToken);

        log.info("Password reset token generated for user: {}", email);

        emailService.sendResetPasswordEmail(
                user.getEmail(),
                token,
                LocaleContextHolder.getLocale()
        );
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        String tokenValue = request.getToken().trim();

        ActivationToken activationToken = activationTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(() -> new InvalidToken("Token not found"));

        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            activationTokenRepository.delete(activationToken);
            throw new InvalidToken("Token expired");
        }

        User user = activationToken.getUser();

        if (user == null) {
            throw new InvalidToken("Invalid token");
        }

        if (!user.getIsActive()) {
            throw new AccountNotActivatedException("Account not activated");
        }

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be same as old password");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        activationTokenRepository.delete(activationToken);
        log.info("Password successfully reset for user: {}", user.getEmail());
    }
}