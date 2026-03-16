package org.project.ttnecommerce.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CategoryRepository categoryRepository;
    private final CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;

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
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIsActive(false);
        user.setIsDeleted(false);
        user.setIsLocked(false);
        user.setIsExpired(false);

        Role sellerRole = roleRepository
                .findByAuthority("ROLE_SELLER")
                .orElseThrow(() -> new RuntimeException("Seller role not found"));

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
        AddressRequest addr = request.getAddress();
        if (addr != null) {
            Address address = new Address();
            address.setAddressLine(addr.getAddressLine());
            address.setCity(addr.getCity());
            address.setState(addr.getState());
            address.setCountry(addr.getCountry());
            address.setZipCode(addr.getZipCode());
            address.setLabel("COMPANY");
            address.setUser(user);
            addressRepository.save(address);
        }
    }

    // get profile method
    public SellerProfileResponse getSellerProfile() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getSeller() == null) {
            throw new InvalidRequestException("User is not a seller");
        }
        Seller seller = user.getSeller();
        Address address = user.getAddresses()
                .stream()
                .findFirst()
                .orElse(null);

        AddressResponse addressResponse = null;
        if (address != null) {
            addressResponse = AddressResponse.builder()
                    .addressId(address.getId())
                    .addressLine(address.getAddressLine())
                    .city(address.getCity())
                    .state(address.getState())
                    .country(address.getCountry())
                    .zipCode(address.getZipCode())
                    .build();
        }
        Path jpg = Paths.get(BASE_PATH + user.getId() + ".jpg");
        Path jpeg = Paths.get(BASE_PATH + user.getId() + ".jpeg");
        Path png = Paths.get(BASE_PATH + user.getId() + ".png");
        Path bmp = Paths.get(BASE_PATH + user.getId() + ".bmp");
        String imagePath = null;
        if (Files.exists(jpg)) {
            imagePath = "/uploads/users/" + user.getId() + ".jpg";
        }
        else if (Files.exists(jpeg)) {
            imagePath = "/uploads/users/" + user.getId() + ".jpeg";
        }
        else if (Files.exists(png)) {
            imagePath = "/uploads/users/" + user.getId() + ".png";
        }
        return SellerProfileResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(user.getIsActive())
                .companyName(seller.getCompanyName())
                .companyContact(seller.getCompanyContact())
                .gst(seller.getGst())
                .image(imagePath)
                .address(addressResponse)
                .build();
    }

    // update profile method
    @Transactional
    public void updateSellerProfile(SellerProfileUpdateRequest request) {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getSeller() == null) {
            throw new InvalidRequestException("User is not a seller");
        }
        Seller seller = user.getSeller();

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getCompanyName() != null) {
            seller.setCompanyName(request.getCompanyName());
        }
        if (request.getCompanyContact() != null) {
            seller.setCompanyContact(request.getCompanyContact());
        }
        if (request.getGst() != null) {
            seller.setGst(request.getGst());
        }
        if (request.getAddress() != null) {
            Address address = user.getAddresses()
                    .stream()
                    .findFirst()
                    .orElseGet(Address::new);

            AddressRequest addr = request.getAddress();

            if (addr.getCity() != null) {
                address.setCity(addr.getCity());
            }
            if (addr.getState() != null) {
                address.setState(addr.getState());
            }
            if (addr.getCountry() != null) {
                address.setCountry(addr.getCountry());
            }
            if (addr.getAddressLine() != null) {
                address.setAddressLine(addr.getAddressLine());
            }
            if (addr.getZipCode() != null) {
                address.setZipCode(addr.getZipCode());
            }
            address.setUser(user);
            if (address.getId() == null) {
                user.getAddresses().add(address);
            }
        }
        userRepository.save(user);
    }

    // update seller password method
    @Transactional
    public void updateSellerPassword(UpdatePasswordRequest request) {

        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getSeller() == null) {
            throw new InvalidRequestException("User is not a seller");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidRequestException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidRequestException("New password and confirm password must match");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("New password cannot be same as old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        emailService.sendPasswordChangeEmail(user);
    }


    // update seller address method
    @Transactional
    public void updateAddress(UUID addressId, UpdateAddressRequest request) {

        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (Boolean.TRUE.equals(user.getIsDeleted()))
            throw new InvalidRequestException("User account is deleted");

        if (!Boolean.TRUE.equals(user.getIsActive()))
            throw new InvalidRequestException("User account is not active");

        if (user.getSeller() == null)
            throw new InvalidRequestException("User is not a seller");

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new InvalidRequestException("Address not found"));

        if (!address.getUser().getId().equals(user.getId()))
            throw new InvalidRequestException("Address does not belong to this seller");

        if (request.getAddressLine() != null)
            address.setAddressLine(request.getAddressLine());

        if (request.getCity() != null)
            address.setCity(request.getCity());

        if (request.getState() != null)
            address.setState(request.getState());

        if (request.getCountry() != null)
            address.setCountry(request.getCountry());

        if (request.getZipCode() != null)
            address.setZipCode(request.getZipCode());

        addressRepository.save(address);
    }




    // get category method
    @Transactional
    public List<SellerCategoryResponse> getCategory() {

        List<Category> allCategories = categoryRepository.findByIsDeletedFalse();

        if (allCategories.isEmpty()) {
            throw new ResourceNotFoundException("No categories found");
        }

        List<SellerCategoryResponse> categoryResponses = new ArrayList<>();

        for (Category category : allCategories) {
            if (category.getChildren() != null && !category.getChildren().isEmpty()) {
                continue;
            }

            List<CategoryMetadataFieldValues> categoryMetadataValues =
                    categoryMetadataFieldValuesRepository.findByCategory(category);

            Map<UUID, MetadataFieldWithValuesResponse> metadataMap = new LinkedHashMap<>();

            for (CategoryMetadataFieldValues metadata : categoryMetadataValues) {

                UUID fieldId = metadata.getMetadataField().getId();
                String fieldName = metadata.getMetadataField().getName();

                List<String> values = Arrays.stream(metadata.getValue().split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());

                if (!metadataMap.containsKey(fieldId)) {
                    metadataMap.put(fieldId,
                            new MetadataFieldWithValuesResponse(fieldId, fieldName, new ArrayList<>(values)));
                } else {
                    metadataMap.get(fieldId).getValues().addAll(values);
                }
            }

            List<MetadataFieldWithValuesResponse> metadataResponses = new ArrayList<>(metadataMap.values());

            List<String> parentCategoryChain = new ArrayList<>();
            Category parent = category.getParentCategory();

            while (parent != null) {
                parentCategoryChain.add(parent.getName());
                parent = parent.getParentCategory();
            }
            Collections.reverse(parentCategoryChain);
            categoryResponses.add(
                    new SellerCategoryResponse(
                            category.getId(),
                            category.getName(),
                            parentCategoryChain,
                            metadataResponses
                    )
            );
        }

        return categoryResponses;
    }






    //upload image method
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
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder
                        .getContext()
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