package org.project.ttnecommerce.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.*;
import org.project.ttnecommerce.entity.*;
import org.project.ttnecommerce.exception.*;
import org.project.ttnecommerce.repository.*;
import org.project.ttnecommerce.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
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
    private final CategoryRepository categoryRepository;


    private static final String BASE_PATH = "uploads/users/";

    // Register Customer method
    @Transactional
    public void registerCustomer(RegisterCustomerRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Role customerRole = roleRepository.findByAuthority("ROLE_CUSTOMER").orElseThrow(() -> new RuntimeException("Customer role not found"));

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

    // activate Customer method
    @Transactional
    public void activateCustomer(String token) {

        ActivationToken activationToken = activationTokenRepository.findByToken(token).orElseThrow(() -> new InvalidRequestException("Invalid activation token"));

        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Activation token expired");
        }

        User user = activationToken.getUser();
        user.setIsActive(true);

        userRepository.save(user);
        activationTokenRepository.delete(activationToken);
    }

    // resend Activation method
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

    // get Customer Profile method
    public CustomerProfileResponse getCustomerProfile() {
        User user = getCurrentAuthenticatedUser();

        if (user.getCustomer() == null) {
            throw new InvalidRequestException("User is not a customer");
        }

        Customer customer = user.getCustomer();
        Path jpg = Paths.get(BASE_PATH + user.getId() + ".jpg");
        Path png = Paths.get(BASE_PATH + user.getId() + ".png");
        String imagePath = null;
        if (Files.exists(jpg)) {
            imagePath = "/uploads/users/" + user.getId() + ".jpg";
        } else if (Files.exists(png)) {
            imagePath = "/uploads/users/" + user.getId() + ".png";
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

    // update customer profile method
    @Transactional
    public void updateCustomerProfile(UpdateCustomerProfileRequest request) {
        User user = getCurrentAuthenticatedUser();
        validateActiveUser(user);
        Customer customer = user.getCustomer();

        boolean updated = false;

        if (hasText(request.getFirstName())) {
            user.setFirstName(request.getFirstName().trim());
            updated = true;
        }

        if (hasText(request.getLastName())) {
            user.setLastName(request.getLastName().trim());
            updated = true;
        }

        if (hasText(request.getContact())) {
            String newContact = request.getContact().trim();

            if (newContact.equals(customer.getContact())) {
                throw new InvalidRequestException("New contact must be different from current contact");
            }

            if (customerRepository.existsByContact(newContact)) {
                throw new InvalidRequestException("Contact number already exists");
            }

            customer.setContact(newContact);
            updated = true;
        }

        if (!updated) {
            throw new InvalidRequestException("At least one valid field must be provided for update");
        }

        userRepository.save(user);
    }

    // add customer address method
    @Transactional
    public void addAddress(AddAddressRequest request) {
        User user = getCurrentAuthenticatedUser();
        validateActiveUser(user);

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

    // get customer address method

    public List<AddAddressResponse> getMyAddresses() {
        User user = getCurrentAuthenticatedUser();
        validateActiveUser(user);

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

    // update customer address method
    @Transactional
    public void updateAddress(UUID addressId, UpdateAddressRequest request) {
        User user = getCurrentAuthenticatedUser();
        validateActiveUser(user);

        Address address = addressRepository.findById(addressId).orElseThrow(() -> new InvalidRequestException("Address not found"));

        boolean updated = false;

        if (hasText(request.getAddressLine())) {
            address.setAddressLine(request.getAddressLine().trim());
            updated = true;
        }

        if (hasText(request.getCity())) {
            address.setCity(request.getCity().trim());
            updated = true;
        }

        if (hasText(request.getState())) {
            address.setState(request.getState().trim());
            updated = true;
        }

        if (hasText(request.getCountry())) {
            address.setCountry(request.getCountry().trim());
            updated = true;
        }

        if (hasText(request.getZipCode())) {
            address.setZipCode(request.getZipCode().trim());
            updated = true;
        }

        if (hasText(request.getLabel())) {
            address.setLabel(request.getLabel().trim());
            updated = true;
        }

        if (!updated) {
            throw new InvalidRequestException("At least one field must be provided for update");
        }

        addressRepository.save(address);
    }

    // delete customer method
    @Transactional
    public void deleteAddress(UUID addressId) {

        User user = getCurrentAuthenticatedUser();
        validateActiveUser(user);

        Address address = addressRepository.findById(addressId).orElseThrow(() -> new InvalidRequestException("Address not found"));
        addressRepository.delete(address);
    }

    // customer change password method
    @Transactional
    public void changePassword(UpdatePasswordRequest request) {
        User user = getCurrentAuthenticatedUser();
        validateActiveUser(user);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidRequestException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidRequestException("New password and confirm password must match");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        emailService.sendPasswordChangeEmail(user);
    }


    // customerCategoryResponse method
    @Transactional
    public List<CustomerCategoryResponse> getCategories(UUID categoryId) {
        List<Category> categories;

        if (categoryId == null) {

            // root categories
            categories = categoryRepository.findByParentCategoryIsNullAndIsDeletedFalse();

        } else {

            Category parent = categoryRepository
                    .findByIdAndIsDeletedFalse(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            categories = categoryRepository
                    .findByParentCategoryAndIsDeletedFalse(parent);
        }

        return categories.stream()
                .map(c -> new CustomerCategoryResponse(
                        c.getId(),
                        c.getName()
                ))
                .toList();
    }












    // upload customer profile method
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

        User user = getCurrentAuthenticatedUser();

        try {
            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = user.getId() + extension;
            Path path = Paths.get(BASE_PATH + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
    }


    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidRequestException("Invalid or missing access token");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new InvalidRequestException("Invalid or missing access token");
        }

        return userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private void validateActiveUser(User user) {

        if (Boolean.TRUE.equals(user.getIsDeleted()))
            throw new InvalidRequestException("User account is deleted");

        if (!Boolean.TRUE.equals(user.getIsActive()))
            throw new InvalidRequestException("User account is not active");

        if (Boolean.TRUE.equals(user.getIsLocked()))
            throw new InvalidRequestException("User account is locked");

        if (Boolean.TRUE.equals(user.getIsExpired()))
            throw new InvalidRequestException("User account is expired");
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}