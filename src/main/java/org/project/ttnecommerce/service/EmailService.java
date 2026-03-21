package org.project.ttnecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.ttnecommerce.entity.Product;
import org.project.ttnecommerce.entity.User;
import org.project.ttnecommerce.i18n.MessageTranslator;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final MessageTranslator translator;

    @Async
    public void sendActivationEmail(String email, String token, Locale locale) {
        String activationLink = "http://localhost:8080/api/customers/activate-customer?token=" + token;
        String subject = translate(locale, "email.activation.subject");
        String message = translate(locale, "email.activation.body", activationLink);

        sendEmail(email, subject, message);
    }

    @Async
    public void sendResetPasswordEmail(String email, String token, Locale locale) {
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
        String subject = translate(locale, "email.reset.subject");
        String message = translate(locale, "email.reset.body", resetLink);

        sendEmail(email, subject, message);
    }

    @Async
    public void sendAccountLockedEmail(String email, Locale locale) {
        sendEmail(
                email,
                translate(locale, "email.account.locked.subject"),
                translate(locale, "email.account.locked.body")
        );
    }

    @Async
    public void sendCustomerDeactivationEmailByAdmin(User user, Locale locale) {
        sendEmail(
                user.getEmail(),
                translate(locale, "email.account.deactivated.subject"),
                translate(locale, "email.account.deactivated.body", user.getFirstName())
        );
    }

    @Async
    public void sendSellerActivationEmailByAdmin(User user, Locale locale) {
        sendEmail(
                user.getEmail(),
                translate(locale, "email.seller.activated.subject"),
                translate(locale, "email.seller.activated.body", user.getFirstName())
        );
    }

    @Async
    public void sendSellerDeactivationEmailByAdmin(User user, Locale locale) {
        sendEmail(
                user.getEmail(),
                translate(locale, "email.account.deactivated.subject"),
                translate(locale, "email.seller.deactivated.body", user.getFirstName())
        );
    }

    @Async
    public void sendPasswordChangeEmail(User user, Locale locale) {
        sendEmail(
                user.getEmail(),
                translate(locale, "email.password.updated.subject"),
                translate(locale, "email.password.updated.body", user.getFirstName())
        );
    }

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

    public void sendCustomerActivationEmailByAdmin(User user, Locale locale) {
        sendEmail(
                user.getEmail(),
                translate(locale, "email.account.activated.subject"),
                translate(locale, "email.account.activated.body", user.getFirstName())
        );
    }


    @Async
    public void sendProductActivationEmail(Product product, Locale locale) {

        if (product == null || product.getSeller() == null) {
            return;
        }

        User seller = product.getSeller();

        if (seller.getEmail() == null) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(seller.getEmail());
        message.setSubject(translate(locale, "email.product.activated.subject"));
        message.setText(translate(locale, "email.product.activated.body", seller.getFirstName(), product.getName()));

        mailSender.send(message);
    }

    @Async
    public void sendProductDeactivationEmail(Product product, Locale locale) {

        if (product == null || product.getSeller() == null) {
            return;
        }

        User seller = product.getSeller();

        if (seller.getEmail() == null) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(seller.getEmail());
        message.setSubject(translate(locale, "email.product.deactivated.subject"));
        message.setText(translate(locale, "email.product.deactivated.body", seller.getFirstName(), product.getName()));

        mailSender.send(message);
    }

    private String translate(Locale locale, String code, Object... args) {
        return translator.getOrDefault(locale, code, code, args);
    }


}
