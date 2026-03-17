package org.project.ttnecommerce.service;

import lombok.RequiredArgsConstructor;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getIsActive()) {
            throw new AccountNotActivatedException("Account not activated");
        }

        activationTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setUser(user);
        activationToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        activationTokenRepository.save(activationToken);
        emailService.sendResetPasswordEmail(user.getEmail(), token);
    }

    public void resetPassword(ResetPasswordRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        ActivationToken activationToken = activationTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new InvalidToken("Token not found"));

        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            activationTokenRepository.delete(activationToken);
            throw new InvalidToken("Token expired");
        }

        User user = activationToken.getUser();

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        activationTokenRepository.delete(activationToken);
    }

}
