package org.project.ttnecommerce.service;

import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.entity.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    public void sendEmail(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Async
    public void sendAccountLockedEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Account Locked");
        message.setText("Your account has been locked due to multiple failed login attempts.");
        mailSender.send(message);
    }

    public void sendCustomerActivationEmailByAdmin(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Account Activated");
        message.setText("Hello " + user.getFirstName() + ",\n\n" + "Your account has been successfully activated by the admin.");
        mailSender.send(message);
    }

    @Async
    public void sendCustomerDeactivationEmailByAdmin(User user) {
        String subject = "Account Deactivated";
        String message = "Hello " + user.getFirstName() + ",\n\n" + "Your account has been deactivated by the Admin.";
        sendEmail(user.getEmail(), subject, message);
    }

    @Async
    public void sendSellerActivationEmailByAdmin(User user) {
        String subject = "Account Activated";
        String message = "Hello " + user.getFirstName() + ",\n\n" + "Your seller account has been activated by the admin.\n\n";
        sendEmail(user.getEmail(), subject, message);
    }

    @Async
    public void sendSellerDeactivationEmailByAdmin(User user) {
        String subject = "Account Deactivated";
        String message = "Hello " + user.getFirstName() + ",\n\n" + "Your seller account has been deactivated by the Admin.";
        sendEmail(user.getEmail(), subject, message);
    }
}