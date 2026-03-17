package org.project.ttnecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.ttnecommerce.entity.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendActivationEmail(String email, String token) {
        String activationLink = "http://localhost:8080/api/customers/activate-customer?token=" + token;
        String subject = "Activate your account";
        String message = "Welcome!\n\n" +
                "Click the link below to activate your account:\n\n" + activationLink +
                "\n\nThis link will expire in 3 hours.";

        sendEmail(email, subject, message);
    }

    @Async
    public void sendResetPasswordEmail(String email, String token) {
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
        String subject = "Reset Your Password";
        String message = "We received a request to reset your password.\n\n" +
                "Click the link below to reset it:\n\n" +
                resetLink + "\n\nThis link will expire in 15 minutes.\n\n" +
                "If you did not request this, please ignore this email.";

        sendEmail(email, subject, message);
    }

    @Async
    public void sendAccountLockedEmail(String email) {
        sendEmail(
                email,
                "Account Locked",
                "Your account has been locked due to multiple failed login attempts."
        );
    }

    @Async
    public void sendCustomerDeactivationEmailByAdmin(User user) {
        sendEmail(
                user.getEmail(),
                "Account Deactivated",
                "Hello " + user.getFirstName() + ",\n\nYour account has been deactivated by the Admin."
        );
    }

    @Async
    public void sendSellerActivationEmailByAdmin(User user) {
        sendEmail(
                user.getEmail(),
                "Account Activated",
                "Hello " + user.getFirstName() + ",\n\nYour seller account has been activated by the admin."
        );
    }

    @Async
    public void sendSellerDeactivationEmailByAdmin(User user) {
        sendEmail(
                user.getEmail(),
                "Account Deactivated",
                "Hello " + user.getFirstName() + ",\n\nYour seller account has been deactivated by the Admin."
        );
    }

    @Async
    public void sendPasswordChangeEmail(User user) {
        sendEmail(
                user.getEmail(),
                "Password Updated Successfully",
                "Hello " + user.getFirstName() + ", your password has been changed successfully."
        );
    }

    // 🔴 IMPORTANT: NOT async
    // Centralized email sender with logging + error handling
    public void sendEmail(String email, String subject, String text) {
        try {
            log.info("Sending email to: {}", email);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

            log.info("Email sent successfully to: {}", email);

        } catch (Exception e) {
            log.error("Failed to send email to: {}", email, e);
        }
    }

    public void sendCustomerActivationEmailByAdmin(User user) {
        sendEmail(
                user.getEmail(),
                "Account Activated",
                "Hello " + user.getFirstName() + ",\n\nYour account has been successfully activated by the admin."
        );
    }
}