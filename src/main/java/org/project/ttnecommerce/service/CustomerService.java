package org.project.ttnecommerce.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.RegisterCustomerRequest;
import org.project.ttnecommerce.entity.*;
import org.project.ttnecommerce.exception.AccountAlreadyActivatedException;
import org.project.ttnecommerce.exception.EmailAlreadyExistsException;
import org.project.ttnecommerce.exception.PasswordMismatchException;
import org.project.ttnecommerce.exception.UserNotFoundException;
import org.project.ttnecommerce.repository.ActivationTokenRepository;
import org.project.ttnecommerce.repository.CustomerRepository;
import org.project.ttnecommerce.repository.RoleRepository;
import org.project.ttnecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerCustomer(RegisterCustomerRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Role customerRole = roleRepository
                .findByAuthority("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Customer role not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIsActive(false);
        user.setIsDeleted(false);
        user.setIsLocked(false);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(customerRole);
        user.getUserRoles().add(userRole);
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setContact(request.getPhone());
        customer.setUser(user);
        user.setCustomer(customer);
        customerRepository.save(customer);

        String token = UUID.randomUUID().toString();

        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setUser(user);
        activationToken.setExpiryDate(LocalDateTime.now().plusHours(3));

        activationTokenRepository.save(activationToken);

        emailService.sendActivationEmail(user.getEmail(), token);
    }

    @Transactional
    public void activateCustomer(String token) {

        ActivationToken activationToken = activationTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));

        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Activation token expired");
        }

        User user = activationToken.getUser();
        user.setIsActive(true);
        userRepository.save(user);

        activationTokenRepository.delete(activationToken);
    }


    @Transactional
    public void resendActivationLink(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with given email does not exist"));

        if (user.getIsActive()) {
            throw new AccountAlreadyActivatedException("Account already activated");
        }

        activationTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setUser(user);
        activationToken.setExpiryDate(LocalDateTime.now().plusHours(3));

        activationTokenRepository.save(activationToken);

        emailService.sendActivationEmail(user.getEmail(), token);
    }
}