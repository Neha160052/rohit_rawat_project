package org.project.ttnecommerce.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.RegisterCustomerRequest;
import org.project.ttnecommerce.entity.Customer;
import org.project.ttnecommerce.entity.Role;
import org.project.ttnecommerce.entity.User;
import org.project.ttnecommerce.entity.UserRole;
import org.project.ttnecommerce.repository.CustomerRepository;
import org.project.ttnecommerce.repository.RoleRepository;
import org.project.ttnecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerCustomer(RegisterCustomerRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role customerRole = roleRepository.findByAuthority("Customer").orElseThrow(() -> new RuntimeException("customer not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIsActive(false);
        user.setIsDeleted(false);
        user.setIsLocked(false);
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setContact(request.getPhone());
        user.setCustomer(customer);
        customer.setUser(user);
        customerRepository.save(customer);

    }
}
