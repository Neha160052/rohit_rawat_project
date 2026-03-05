package org.project.ttnecommerce.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.RegisterSellerRequest;
import org.project.ttnecommerce.entity.*;
import org.project.ttnecommerce.exception.*;
import org.project.ttnecommerce.repository.RoleRepository;
import org.project.ttnecommerce.repository.SellerRepository;
import org.project.ttnecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerSeller(RegisterSellerRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if (sellerRepository.existsByGst(request.getGst())) {
            throw new GstAlreadyExistsException("GST already registered");
        }

        if (sellerRepository.existsByCompanyNameIgnoreCase(request.getCompanyName())) {
            throw new CompanyAlreadyExistsException("Company name already exists");
        }

        Role sellerRole = roleRepository
                .findByAuthority("SELLER")
                .orElseThrow(() -> new RuntimeException("Seller role not found"));

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        user.setIsActive(true);
        user.setIsDeleted(false);
        user.setIsLocked(false);
        user.setIsExpired(false);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(sellerRole);

        user.getUserRoles().add(userRole);

        userRepository.save(user);

        Seller seller = new Seller();

        seller.setUser(user);
        seller.setGst(request.getGst());
        seller.setCompanyName(request.getCompanyName());
        seller.setCompanyAddress(request.getCompanyAddress());
        seller.setCompanyContact(request.getCompanyContact());
        seller.setIsApproved(false);

        sellerRepository.save(seller);
    }
}