package org.project.ttnecommerce.bootstrap;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.entity.Role;
import org.project.ttnecommerce.entity.User;
import org.project.ttnecommerce.entity.UserRole;
import org.project.ttnecommerce.repository.RoleRepository;
import org.project.ttnecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) {
        String adminEmail = "admin@gmail.com";
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        Role adminRole = roleRepository.findByAuthority("ADMIN").orElseGet(() -> {
                    Role role = new Role();
                    role.setAuthority("ADMIN");
                    return roleRepository.save(role);
                });

        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("Admin@07"));
        admin.setFirstName("Admin");
        admin.setLastName("System");
        admin.setIsActive(true);
        admin.setIsDeleted(false);
        admin.setIsLocked(false);
        admin.setIsExpired(false);

        UserRole userRole = new UserRole();
        userRole.setUser(admin);
        userRole.setRole(adminRole);
        admin.getUserRoles().add(userRole);
        userRepository.save(admin);
        System.out.println("Admin account has been created");
    }
}