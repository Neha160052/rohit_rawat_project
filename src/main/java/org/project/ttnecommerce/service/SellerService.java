package org.project.ttnecommerce.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.AddressRequest;
import org.project.ttnecommerce.dto.AddressResponse;
import org.project.ttnecommerce.dto.RegisterSellerRequest;
import org.project.ttnecommerce.dto.SellerProfileResponse;
import org.project.ttnecommerce.entity.*;
import org.project.ttnecommerce.exception.*;
import org.project.ttnecommerce.repository.AddressRepository;
import org.project.ttnecommerce.repository.RoleRepository;
import org.project.ttnecommerce.repository.SellerRepository;
import org.project.ttnecommerce.repository.UserRepository;
import org.project.ttnecommerce.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String BASE_PATH = "uploads/users/";

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

        Role sellerRole = roleRepository.findByAuthority("SELLER").orElseThrow(() -> new RuntimeException("Seller role not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        user.setIsActive(false);
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
        seller.setCompanyContact(request.getCompanyContact());
        seller.setIsApproved(false);

        sellerRepository.save(seller);

        AddressRequest addressRequest = request.getAddress();

        Address address = new Address();
        address.setAddressLine(addressRequest.getAddressLine());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setCountry(addressRequest.getCountry());
        address.setZipCode(addressRequest.getZipCode());
        address.setLabel("Company");
        address.setUser(user);

        addressRepository.save(address);
    }




    public void uploadProfileImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidRequestException("File cannot be empty");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new InvalidRequestException("File size must be less than 5MB");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new InvalidRequestException("Only image files are allowed");
        }
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidRequestException("User not found"));
        try {
            String originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String fileName = user.getId() + extension;
            Path path = Paths.get(BASE_PATH + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
    }





}