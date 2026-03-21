package org.project.ttnecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.project.ttnecommerce.dto.ForgotPasswordRequest;
import org.project.ttnecommerce.dto.ResetPasswordRequest;
import org.project.ttnecommerce.dto.ApiResponse;
import org.project.ttnecommerce.i18n.MessageTranslator;
import org.project.ttnecommerce.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final MessageTranslator translator;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password API called | email={}", request.getEmail());
        passwordResetService.forgotPassword(request);
        return ResponseEntity.ok(new ApiResponse(translator.get("response.password.reset.email.sent"), null));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Reset password API called");
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(new ApiResponse(translator.get("response.password.reset.success"), null));
    }
}