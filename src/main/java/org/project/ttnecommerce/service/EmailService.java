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

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Activate your account");

        message.setText(
                "Welcome!\n\n" +
                        "Click the link below to activate your account:\n\n" +
                        activationLink +
                        "\n\nThis link will expire in 3 hours."
        );

        mailSender.send(message);
    }
}