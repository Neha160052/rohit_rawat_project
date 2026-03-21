package org.project.ttnecommerce.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.ttnecommerce.dto.*;
import org.project.ttnecommerce.entity.*;
import org.project.ttnecommerce.exception.*;
import org.project.ttnecommerce.repository.*;
import org.project.ttnecommerce.security.CustomUserDetails;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
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
    private final ProductRepository productRepository;
    private final ProductVariationRepository productVariationRepository;
    private final CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;


    private static final String BASE_PATH = "uploads/users/";

    // Register Customer method
    @Transactional
    public void registerCustomer(RegisterCustomerRequest request) {
        log.info("Customer registration started | email={}", request.getEmail());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Customer registration failed - password mismatch | email={}", request.getEmail());
            throw new PasswordMismatchException("Passwords and Confirm Password do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Customer registration failed - email already exists | email={}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Role customerRole = roleRepository.findByAuthority("ROLE_CUSTOMER")
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
        log.info("EMAIL METHOD CALLED");
        emailService.sendActivationEmail(user.getEmail(), token, LocaleContextHolder.getLocale());
        log.info("Customer registered successfully | email={}", user.getEmail());
    }

    // activate Customer method
    @Transactional
    public void activateCustomer(String token) {
        log.info("Customer activation requested");
        ActivationToken activationToken = activationTokenRepository.findByToken(token).orElseThrow(() -> new InvalidRequestException("Invalid activation token"));

        if (token == null || token.trim().isEmpty()) {
            throw new InvalidRequestException("Activation token is required");
        }

        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Activation token expired");
            throw new InvalidRequestException("Activation token expired");
        }

        User user = activationToken.getUser();
        user.setIsActive(true);

        userRepository.save(user);
        activationTokenRepository.delete(activationToken);
        log.info("Customer account activated | userId={}", user.getId());
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
        emailService.sendActivationEmail(user.getEmail(), token, LocaleContextHolder.getLocale());
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

    // deleteAddress customer method
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

        emailService.sendPasswordChangeEmail(user, LocaleContextHolder.getLocale());
    }


    // customerCategoryResponse method
    @Transactional
    public List<CustomerCategoryResponse> getCategories(UUID categoryId) {
        List<Category> categories;

        if (categoryId == null) {
            categories = categoryRepository.findByParentCategoryIsNullAndIsDeletedFalse();
        }
        else {
            Category parent = categoryRepository.findByIdAndIsDeletedFalse(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            categories = categoryRepository.findByParentCategoryAndIsDeletedFalse(parent);
        }

        return categories.stream()
                .map(c -> new CustomerCategoryResponse(
                        c.getId(),
                        c.getName()
                ))
                .toList();
    }


    //getCategoryFilterDetails method
    @Transactional
    public CategoryFilterResponse getCategoryFilterDetails(UUID categoryId) {

        Category category = categoryRepository.findByIdAndIsDeletedFalse(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        List<Category> categoryTree = new ArrayList<>();
        collectChildCategories(category, categoryTree);

        List<UUID> categoryIds = categoryTree.stream()
                .map(Category::getId)
                .toList();

        List<CategoryMetadataFieldValues> metadataValues = categoryMetadataFieldValuesRepository.findByCategory(category);

        List<MetadataFieldWithValuesResponse> metadataResponses = metadataValues.stream()
                        .map(m -> new MetadataFieldWithValuesResponse(
                                m.getMetadataField().getId(),
                                m.getMetadataField().getName(),
                                Arrays.stream(m.getValue().split(","))
                                        .map(String::trim)
                                        .toList()
                        ))
                        .toList();

        List<String> brands = productRepository.findDistinctBrands(categoryTree);

        List<Object[]> result = productVariationRepository.findMinMaxPrice(categoryIds);

        Double minPrice = null;
        Double maxPrice = null;

        if (!result.isEmpty()) {
            Object[] row = result.get(0);

            if (row[0] != null) {
                minPrice = ((Number) row[0]).doubleValue();
            }

            if (row[1] != null) {
                maxPrice = ((Number) row[1]).doubleValue();
            }
        }
        PriceRangeResponse priceRange = new PriceRangeResponse(minPrice, maxPrice);
        return new CategoryFilterResponse(metadataResponses, brands, priceRange);
    }

    private void collectChildCategories(Category category, List<Category> categoryList) {
        categoryList.add(category);
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            for (Category child : category.getChildren()) {
                collectChildCategories(child, categoryList);
            }
        }
    }


    // get product method
    @Transactional
    public ProductResponse viewProduct(UUID productId) {

        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new InvalidRequestException("Product is not active");
        }

        Category category = product.getCategory();
        if (category == null || Boolean.TRUE.equals(category.getIsDeleted())) {
            throw new InvalidRequestException("Product category is invalid or deleted");
        }

        List<ProductVariation> variations = getValidVariationsOrThrow(product);
        return buildProductResponse(product, variations);
    }

    // get all category product method
    @Transactional
    public List<ProductResponse> viewAllProducts(UUID categoryId, Integer max, Integer offset, String sort, String order) {

        max = (max == null) ? 10 : max;
        offset = (offset == null) ? 0 : offset;
        sort = (sort == null) ? "id" : sort;
        order = (order == null) ? "asc" : order;

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (Boolean.TRUE.equals(category.getIsDeleted())) {
            throw new InvalidRequestException("Category is deleted");
        }

        List<Category> categories = getAllCategories(category);

        List<Product> products =
                productRepository.findValidProductsByCategories(categories);

        if (products.isEmpty()) return List.of();

        List<Product> validProducts = products.stream()
                .filter(p -> !productVariationRepository.findValidVariations(p).isEmpty())
                .toList();

        if (validProducts.isEmpty()) return List.of();

        Comparator<Product> comparator = getComparator(sort);

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        List<Product> sorted = validProducts.stream()
                .sorted(comparator)
                .toList();

        int start = Math.min(offset, sorted.size());
        int end = Math.min(start + max, sorted.size());

        List<Product> paginated = sorted.subList(start, end);

        return paginated.stream()
                .map(p -> buildProductResponse(p,
                        productVariationRepository.findValidVariations(p)))
                .toList();
    }

    private List<ProductVariation> getValidVariationsOrThrow(Product product) {
        List<ProductVariation> variations = productVariationRepository.findValidVariations(product);

        if (variations.isEmpty()) {
            throw new InvalidRequestException("No valid variations available for this product");
        }

        return variations;
    }

    private ProductResponse buildProductResponse(Product product, List<ProductVariation> variations) {

        List<VariationResponse> variationResponses = variations.stream()
                .map(this::mapToVariationResponse)
                .toList();

        List<String> primaryImages = variations.stream()
                .map(v -> {
                    if (v.getPrimaryImageName() != null) {
                        return v.getPrimaryImageName();
                    } else if (v.getImages() != null && !v.getImages().isEmpty()) {
                        return v.getImages().get(0).getImageName();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return ProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .category(mapToCategoryDto(product.getCategory()))
                .primaryImages(primaryImages)
                .variations(variationResponses)
                .build();
    }

    private VariationResponse mapToVariationResponse(ProductVariation v) {

        String primaryImage = v.getPrimaryImageName();

        if (primaryImage == null && v.getImages() != null && !v.getImages().isEmpty()) {
            primaryImage = v.getImages().get(0).getImageName();
        }

        return VariationResponse.builder()
                .id(v.getId())
                .price(v.getPrice())
                .quantity(v.getQuantityAvailable())
                .primaryImage(primaryImage)
                .metadata(v.getMetadata())
                .build();
    }

    private CategoryDto mapToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    private Comparator<Product> getComparator(String sort) {
        if ("name".equalsIgnoreCase(sort)) {
            return Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
        }

        return Comparator.comparing(Product::getId);
    }

    private List<Category> getAllCategories(Category root) {
        List<Category> result = new ArrayList<>();
        result.add(root);

        for (Category child : root.getChildren()) {
            result.addAll(getAllCategories(child));
        }
        return result;
    }



    // get similar product method
    @Transactional
    public List<SimilarProductResponse> getSimilarProducts(UUID productId, Integer max, Integer offset, String sort, String order) {

        max = (max == null || max <= 0) ? 10 : max;
        offset = (offset == null || offset < 0) ? 0 : offset;
        sort = (sort == null) ? "id" : sort;
        order = (order == null) ? "asc" : order;

        Product baseProduct = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!Boolean.TRUE.equals(baseProduct.getIsActive())) {
            throw new InvalidRequestException("Product is not active");
        }

        Category category = baseProduct.getCategory();

        if (category == null || Boolean.TRUE.equals(category.getIsDeleted())) {
            throw new InvalidRequestException("Product category is invalid");
        }

        List<Category> categories = new ArrayList<>();
        collectChildCategories(category, categories);

        List<Product> products = productRepository.findValidProductsByCategories(categories);

        if (products.isEmpty()) return List.of();

        List<Product> filtered = products.stream()
                .filter(p -> !p.getId().equals(productId)) // exclude current
                .filter(p -> !productVariationRepository.findValidVariations(p).isEmpty())
                .toList();

        if (filtered.isEmpty()) return List.of();

        Comparator<Product> comparator = getComparator(sort);

        filtered = filtered.stream()
                .sorted((p1, p2) -> {
                    boolean p1SameBrand = Objects.equals(p1.getBrand(), baseProduct.getBrand());
                    boolean p2SameBrand = Objects.equals(p2.getBrand(), baseProduct.getBrand());

                    if (p1SameBrand && !p2SameBrand) return -1;
                    if (!p1SameBrand && p2SameBrand) return 1;

                    return comparator.compare(p1, p2);
                })
                .toList();

        if ("desc".equalsIgnoreCase(order)) {
            Collections.reverse(filtered);
        }

        int start = Math.min(offset, filtered.size());
        int end = Math.min(start + max, filtered.size());

        List<Product> paginated = filtered.subList(start, end);

        return paginated.stream()
                .map(p -> mapToSimilarProduct(
                        p,
                        productVariationRepository.findValidVariations(p)
                ))
                .toList();
    }

    private SimilarProductResponse mapToSimilarProduct(Product product, List<ProductVariation> variations) {
        List<String> primaryImages = variations.stream()
                .map(v -> {
                    if (v.getPrimaryImageName() != null) {
                        return v.getPrimaryImageName();
                    } else if (v.getImages() != null && !v.getImages().isEmpty()) {
                        return v.getImages().get(0).getImageName();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return SimilarProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .category(mapToCategoryDto(product.getCategory()))
                .primaryImages(primaryImages)
                .build();
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

        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found"));
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
