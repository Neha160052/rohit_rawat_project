package org.project.ttnecommerce.service;

import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.entity.User;
import org.project.ttnecommerce.repository.UserRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final int MAX_ATTEMPTS = 3;

    public void loginFailed(User user){

        int attempts = user.getInvalidAttemptCount() + 1;

        user.setInvalidAttemptCount(attempts);

        if(attempts >= MAX_ATTEMPTS){
            user.setIsLocked(true);
            emailService.sendAccountLockedEmail(user.getEmail(), LocaleContextHolder.getLocale());
        }

        userRepository.save(user);
    }

    public void loginSucceeded(User user){

        user.setInvalidAttemptCount(0);

        userRepository.save(user);
    }
}
