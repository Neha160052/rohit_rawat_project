package org.project.ttnecommerce.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.AddAddressRequest;
import org.project.ttnecommerce.dto.AddAddressResponse;
import org.project.ttnecommerce.dto.CustomerProfileResponse;
import org.project.ttnecommerce.dto.RegisterCustomerRequest;
import org.project.ttnecommerce.entity.*;
import org.project.ttnecommerce.exception.*;
import org.project.ttnecommerce.repository.*;
import org.project.ttnecommerce.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final AddressRepository addressRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final String BASE_PATH = "uploads/users/";

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

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with given email does not exist"));
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

    // getCustomer method

    public CustomerProfileResponse getCustomerProfile() {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.getCustomer() == null) {
            throw new InvalidRequestException("User is not a customer");
        }

        Customer customer = user.getCustomer();
        Path jpg = Paths.get(BASE_PATH + user.getId() + ".jpg");
        Path jpeg = Paths.get(BASE_PATH + user.getId() + ".jpeg");
        Path png = Paths.get(BASE_PATH + user.getId() + ".png");
        Path bmp = Paths.get(BASE_PATH + user.getId() + ".bmp");

        String imagePath = null;
        if (Files.exists(jpg)) {
            imagePath = "/uploads/users/" + user.getId() + ".jpg";
        } else if (Files.exists(jpeg)) {
            imagePath = "/uploads/users/" + user.getId() + ".jpeg";
        } else if (Files.exists(png)) {
            imagePath = "/uploads/users/" + user.getId() + ".png";
        } else if (Files.exists(bmp)) {
            imagePath = "/uploads/users/" + user.getId() + ".bmp";
        }

        return CustomerProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(user.getIsActive())
                .contact(customer.getContact())
                .profileImage(imagePath)
                .build();
    }


    // Customer getMyAddress method
    public List<AddAddressResponse> getMyAddresses() {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!user.getIsActive()) {
            throw new InvalidRequestException("User account is not active");
        }
        if (user.getCustomer() == null) {
            throw new InvalidRequestException("User is not a customer");
        }
        return user.getAddresses()
                .stream()
                .sorted(Comparator.comparing(Address::getId))
                .map(address -> AddAddressResponse.builder()
                        .addressId(address.getId())
                        .city(address.getCity())
                        .state(address.getState())
                        .country(address.getCountry())
                        .addressLine(address.getAddressLine())
                        .label(address.getLabel())
                        .zipCode(address.getZipCode())
                        .build())
                .toList();
    }


    // customer addAddress method
    public void addAddress(AddAddressRequest request) {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!user.getIsActive()) {
            throw new InvalidRequestException("User account is not active");
        }
        if (user.getCustomer() == null) {
            throw new InvalidRequestException("User is not a customer");
        }

        Address address = Address.builder()
                .addressLine(request.getAddressLine().trim())
                .city(request.getCity().trim())
                .state(request.getState().trim())
                .country(request.getCountry().trim())
                .zipCode(request.getZipCode().trim())
                .label(request.getLabel().trim())
                .user(user)
                .build();

        addressRepository.save(address);
    }

    // upload image method
    public void uploadProfileImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("File cannot be empty");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new InvalidRequestException("File size must be less than 5MB");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new InvalidRequestException("Only image files are allowed");
        }
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        try {
            String originalName = file.getOriginalFilename();
            if (originalName == null || !originalName.contains(".")) {
                throw new InvalidRequestException("Invalid file name");
            }
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
