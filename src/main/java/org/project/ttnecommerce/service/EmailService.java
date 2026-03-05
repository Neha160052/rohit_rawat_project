package org.project.ttnecommerce.service;

import lombok.RequiredArgsConstructor;
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

        String activationLink =
                "http://localhost:8080/api/customers/activate-customer?token=" + token;

        String subject = "Activate your account";

        String message =
                "Welcome!\n\n" +
                        "Click the link below to activate your account:\n\n" +
                        activationLink +
                        "\n\nThis link will expire in 3 hours.";

        sendEmail(email, subject, message);
    }


    @Async
    public void sendResetPasswordEmail(String email, String token) {

        String resetLink =
                "http://localhost:8080/auth/reset-password?token=" + token;

        String subject = "Reset Your Password";

        String message =
                "We received a request to reset your password.\n\n" +
                        "Click the link below to reset it:\n\n" +
                        resetLink +
                        "\n\nThis link will expire in 15 minutes.\n\n" +
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
}