package org.project.ttnecommerce.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.project.ttnecommerce.dto.ForgotPasswordRequest;
import org.project.ttnecommerce.dto.ResetPasswordRequest;
import org.project.ttnecommerce.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Password reset API called: Forgot password | email={}", request.getEmail());
        passwordResetService.forgotPassword(request);
        return ResponseEntity.ok("Reset password email sent");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Password reset API called: Reset password");
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok("Password successfully updated");
    }
}
