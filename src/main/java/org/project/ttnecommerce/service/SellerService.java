package org.project.ttnecommerce.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.*;
import org.project.ttnecommerce.entity.*;
import org.project.ttnecommerce.exception.*;
import org.project.ttnecommerce.exception.AccessDeniedException;
import org.project.ttnecommerce.repository.*;
import org.project.ttnecommerce.security.CustomUserDetails;
import org.project.ttnecommerce.specification.SellerProductSpecification;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CategoryRepository categoryRepository;
    private final CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final ProductVariationImageRepository productVariationImageRepository;
    private final ProductVariationRepository variationRepository;

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
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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

            List<CategoryMetadataFieldValues> categoryMetadataValues = categoryMetadataFieldValuesRepository.findByCategory(category);

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
                }
                else {
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


    // add product method
    @Transactional
    public String addProduct(AddProductRequest request) {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User seller = userDetails.getUser();

        if (!seller.getIsActive() || seller.getIsDeleted()) {
            throw new InvalidRequestException("Seller account is not active");
        }

        if (seller.getSeller() == null || !seller.getSeller().getIsApproved()) {
            throw new InvalidRequestException("Seller is not approved");
        }

        String name = request.getName().trim().toLowerCase();
        String brand = request.getBrand().trim().toLowerCase();

        if (name.isEmpty()) {
            throw new InvalidInputException("Product name cannot be empty");
        }

        if (brand.isEmpty()) {
            throw new InvalidInputException("Brand cannot be empty");
        }

        Category category = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (categoryRepository.existsByParentCategoryAndIsDeletedFalse(category)) {
            throw new InvalidRequestException("Product can only be added to leaf category");
        }

        boolean exists = productRepository.existsBySellerIdAndNameAndBrandAndCategoryIdAndIsDeletedFalse(seller.getId(), name, brand, category.getId());

        if (exists) {
            throw new InvalidRequestException("Product already exists");
        }

        String description = request.getDescription();
        if (description != null && description.length() > 500) {
            throw new InvalidRequestException("Description too long");
        }

        Product product = new Product();
        product.setSeller(seller);
        product.setName(name);
        product.setBrand(brand);
        product.setDescription(description);
        product.setCategory(category);
        product.setIsCancellable(Boolean.TRUE.equals(request.getIsCancellable()));
        product.setIsReturnable(Boolean.TRUE.equals(request.getIsReturnable()));
        product.setIsActive(false);
        product.setIsDeleted(false);
        try {
            productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException("Duplicate product detected");
        }

        try {
            emailService.sendEmail(
                    "admin@ecommerce.com",
                    "New Product Added",
                    "Seller " + seller.getEmail() +
                            " added product: " + name +
                            " (Brand: " + brand + ") awaiting approval."
            );
        } catch (Exception e) {
        }

        return "Product created successfully and is inactive until admin approval";
    }

    // add Product Variation method

    @Transactional
    public String addProductVariation(AddProductVariationRequest request) {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User seller = userDetails.getUser();

        if (Boolean.TRUE.equals(seller.getIsDeleted()) || !Boolean.TRUE.equals(seller.getIsActive())) {
            throw new InvalidRequestException("Seller account is not active");
        }

        if (seller.getSeller() == null || !Boolean.TRUE.equals(seller.getSeller().getIsApproved())) {
            throw new InvalidRequestException("Seller is not approved");
        }


        Product product = productRepository.findByIdAndIsDeletedFalse(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getIsActive()) {
            throw new InvalidRequestException("Product is not active");
        }

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new InvalidRequestException("Unauthorized access to product");
        }

        if (request.getQuantityAvailable() < 0) {
            throw new InvalidRequestException("Quantity cannot be negative");
        }

        if (request.getPrice() < 0) {
            throw new InvalidRequestException("Price cannot be negative");
        }

        Map<String, String> metadataMap;

        try {

            metadataMap = objectMapper.readValue(request.getMetadata(), Map.class);
        }
        catch (Exception e) {
            throw new InvalidRequestException("Invalid metadata format (must be JSON)");
        }

        if (metadataMap == null || metadataMap.isEmpty()) {
            throw new InvalidRequestException("Metadata cannot be empty");
        }

        Map<String, String> normalizedMetadata = new HashMap<>();

        for (Map.Entry<String, String> entry : metadataMap.entrySet()) {
            String key = entry.getKey().toLowerCase().trim();
            String value = entry.getValue().toLowerCase().trim();

            if (key.isEmpty() || value.isEmpty()) {
                throw new InvalidRequestException("Invalid metadata key/value");
            }

            normalizedMetadata.put(key, value);
        }

        metadataMap = normalizedMetadata;

        List<CategoryMetadataFieldValues> allowed = categoryMetadataFieldValuesRepository.findByCategory(product.getCategory());

        Map<String, Set<String>> validMap = new HashMap<>();

        for (CategoryMetadataFieldValues field : allowed) {

            String fieldName = field.getMetadataField().getName().toLowerCase();

            Set<String> values = Arrays.stream(field.getValue().split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            validMap.computeIfAbsent(fieldName, k -> new HashSet<>())
                    .addAll(values);
        }

        if (metadataMap.size() != validMap.size()) {
            throw new InvalidRequestException("Metadata structure mismatch");
        }

        for (Map.Entry<String, String> entry : metadataMap.entrySet()) {

            if (!validMap.containsKey(entry.getKey())) {
                throw new InvalidRequestException("Invalid metadata field: " + entry.getKey());
            }

            if (!validMap.get(entry.getKey()).contains(entry.getValue())) {
                throw new InvalidRequestException("Invalid value for field: " + entry.getKey());
            }
        }

        List<ProductVariation> existing = variationRepository.findByProductAndIsDeletedFalse(product, Sort.by(Sort.Direction.ASC, "id"));

        for (ProductVariation pv : existing) {
            try {
                Map<String, String> existingMeta =
                        objectMapper.readValue(pv.getMetadata(), Map.class);

                if (existingMeta.equals(metadataMap)) {
                    throw new InvalidRequestException("Duplicate variation already exists");
                }

            } catch (Exception e) {
                throw new InvalidRequestException("Metadata comparison failed");
            }
        }

        ProductVariation variation = new ProductVariation();
        variation.setProduct(product);
        variation.setQuantityAvailable(request.getQuantityAvailable());
        variation.setPrice(request.getPrice());
        variation.setIsActive(true);
        variation.setIsDeleted(false);

        try {
            variation.setMetadata(objectMapper.writeValueAsString(metadataMap));
        }
        catch (Exception e) {
            throw new InvalidRequestException("Metadata processing failed");
        }

        variationRepository.save(variation);

        String primaryImageName = fileStorageService.storeProductVariationImage(request.getPrimaryImage(),
                product.getId(),
                variation.getId(),
                true,
                0
        );

        variation.setPrimaryImageName(primaryImageName);

        if (request.getSecondaryImages() != null) {
            int index = 1;
            for (MultipartFile file : request.getSecondaryImages()) {

                String fileName = fileStorageService.storeProductVariationImage(file, product.getId(),
                        variation.getId(),
                        false,
                        index++
                );

                ProductVariationImage img = new ProductVariationImage();
                img.setProductVariation(variation);
                img.setImageName(fileName);
                img.setIsPrimary(false);

                productVariationImageRepository.save(img);
            }
        }

        variationRepository.save(variation);

        return "Product variation created successfully";
    }

    // seller get-product
    @Transactional
    public List<SellerProductResponse> getSellerProducts(SellerProductFilterRequest request) {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getSeller() == null) {
            throw new AccessDeniedException("User is not a seller");
        }

        UUID sellerId = user.getId();

        if (request.getProductId() != null) {
            validateProductAccess(request.getProductId(), sellerId);
        }

        Sort sort = Sort.by(request.getOrder().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, request.getSort());
        int page = request.getOffset() / request.getMax();
        Pageable pageable = PageRequest.of(page, request.getMax(), sort);

        Specification<Product> spec = SellerProductSpecification.filterProducts(sellerId, request.getProductId(), request.getCategoryId());
        Page<Product> pageResult = productRepository.findAll(spec, pageable);
        int start = request.getOffset() % request.getMax();
        List<Product> content = pageResult.getContent();

        if (start >= content.size()) {
            return Collections.emptyList();
        }

        List<Product> sliced = content.subList(start, content.size());

        return sliced.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateProductAccess(UUID productId, UUID sellerId) {

        Product product = productRepository
                .findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new AccessDeniedException("Not your product");
        }
    }

    private SellerProductResponse mapToResponse(Product product) {
        CategoryResponse category = new CategoryResponse();
        category.setId(product.getCategory().getId());
        category.setName(product.getCategory().getName());

        return SellerProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .isActive(product.getIsActive())
                .category(category)
                .build();
    }


    // get product variation method
    @Transactional
    public VariationPageResponse getVariations(UUID productId, Integer max, Integer offset, String sort, String order, String query, UUID productVariationId) {
        if (productId == null) {
            throw new InvalidRequestException("Product ID is mandatory");
        }

        if (max == null || max < 1 || max > 50) {
            throw new InvalidRequestException("'max' must be between 1 and 50");
        }

        if (offset == null || offset < 0) {
            throw new InvalidRequestException("'offset' cannot be negative");
        }

        List<String> allowedSort = List.of("id", "price", "quantity");
        if (sort == null || !allowedSort.contains(sort.toLowerCase())) {
            throw new InvalidRequestException("Invalid sort field");
        }

        if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
            throw new InvalidRequestException("Invalid order");
        }

        Map<String, String> sortMap = Map.of(
                "id", "id",
                "price", "price",
                "quantity", "quantityAvailable"
        );

        String resolvedSort = sortMap.get(sort.toLowerCase());

        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sortObj = Sort.by(direction, resolvedSort);

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getSeller() == null) {
            throw new AccessDeniedException("Only sellers allowed");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new InvalidRequestException("User not active");
        }

        Product product = productRepository.findByIdAndSellerIdAndIsDeletedFalse(productId, user.getSeller().getId())
                .orElseThrow(() -> new AccessDeniedException("Product not found or not yours"));


        List<ProductVariation> allVariations;

        if (productVariationId != null) {

            ProductVariation variation = variationRepository.findByIdAndProduct(productVariationId, product)
                    .orElseThrow(() -> new ResourceNotFoundException("Variation not found"));

            allVariations = List.of(variation);

        }
        else if (query != null && !query.isBlank()) {

            allVariations = variationRepository.searchByProductWithoutPaging(product, query.trim().toLowerCase(), sortObj);

        }
        else {

            allVariations = variationRepository.findByProductAndIsDeletedFalse(product, sortObj);
        }

        int total = allVariations.size();

        if (offset >= total) {
            return VariationPageResponse.builder().content(Collections.emptyList()).offset(offset).max(max).totalElements(total).build();
        }

        int end = Math.min(offset + max, total);

        List<ProductVariation> paged = allVariations.subList(offset, end);

        List<VariationDetailResponse> content = paged.stream()
                .map(this::mapToVariationDetailResponse)
                .toList();

        return VariationPageResponse.builder()
                .content(content)
                .offset(offset)
                .max(max)
                .totalElements(total)
                .build();
    }

    private VariationDetailResponse mapToVariationDetailResponse(ProductVariation v) {

        List<String> secondaryImages = Collections.emptyList();

        if (v.getImages() != null && !v.getImages().isEmpty()) {
            secondaryImages = v.getImages().stream()
                    .filter(img -> !Boolean.TRUE.equals(img.getIsPrimary()))
                    .map(img -> buildImageUrl(v.getProduct().getId(), img.getImageName()))
                    .toList();
        }

        return VariationDetailResponse.builder()
                .variationId(v.getId())
                .price(v.getPrice())
                .quantityAvailable(v.getQuantityAvailable())
                .metadata(v.getMetadata())
                .primaryImage(buildImageUrl(
                        v.getProduct().getId(),
                        v.getPrimaryImageName()
                ))
                .secondaryImages(secondaryImages)
                .isActive(v.getIsActive())
                .isDeleted(v.getIsDeleted())
                .build();
    }

    private String buildImageUrl(UUID productId, String imageName) {
        if (imageName == null) return null;

        return "/uploads/products/" +
                productId +
                "/variations/" +
                imageName;
    }


    // delete product method
    @Transactional
    public String deleteProduct(UUID productId) {

        if (productId == null) {
            throw new InvalidRequestException("Product ID cannot be null");
        }

        User user = getLoggedInUser();

        if (user.getSeller() == null) {
            throw new AccessDeniedException("User is not a seller");
        }

        UUID sellerId = user.getId();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new AccessDeniedException("You are not allowed to delete this product");
        }

        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new InvalidRequestException("Product already deleted");
        }

        product.setIsDeleted(true);
        product.setIsActive(false);
        productRepository.save(product);

        return "Product deleted successfully";
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }



    // update product method
    @Transactional
    public String updateProduct(UpdateProductRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new InvalidRequestException("Product is deleted");
        }

        User user = getLoggedInUser();

        if (user.getSeller() == null) {
            throw new AccessDeniedException("User is not a seller");
        }

        if (!product.getSeller().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to update this product");
        }

        if (request.getName() == null &&
                request.getDescription() == null &&
                request.getIsCancellable() == null &&
                request.getIsReturnable() == null) {

            throw new InvalidRequestException("At least one field must be provided for update");
        }

        boolean isUpdated = false;

        if (request.getName() != null) {
            String newName = request.getName().trim();

            if (newName.isEmpty()) {
                throw new InvalidInputException("Product name cannot be empty");
            }

            if (!newName.equalsIgnoreCase(product.getName())) {

                boolean exists = productRepository.existsBySellerIdAndNameAndBrandAndCategoryIdAndIsDeletedFalse(
                                user.getId(),
                                newName,
                                product.getBrand(),
                                product.getCategory().getId()
                        );
                if (exists) {
                    throw new InvalidRequestException(
                            "Product with same name already exists for this brand and category"
                    );
                }
                product.setName(newName);
                isUpdated = true;
            }
        }

        if (request.getDescription() != null) {
            String newDesc = request.getDescription().trim();

            if (!newDesc.equals(product.getDescription())) {
                product.setDescription(newDesc);
                isUpdated = true;
            }
        }

        if (request.getIsCancellable() != null &&
                !request.getIsCancellable().equals(product.getIsCancellable())) {

            product.setIsCancellable(request.getIsCancellable());
            isUpdated = true;
        }

        if (request.getIsReturnable() != null &&
                !request.getIsReturnable().equals(product.getIsReturnable())) {

            product.setIsReturnable(request.getIsReturnable());
            isUpdated = true;
        }
        if (!isUpdated) {
            return "No changes Made";
        }

        productRepository.save(product);
        return "Product updated successfully";
    }

    @Transactional
    public String updateProductVariation(UpdateProductVariationRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getSeller() == null) {
            throw new AccessDeniedException("User is not a seller");
        }

        ProductVariation variation = variationRepository
                .findById(request.getVariationId())
                .orElseThrow(() -> new ResourceNotFoundException("Variation not found"));

        if (variation.getIsDeleted()) {
            throw new InvalidRequestException("Variation is deleted");
        }

        Product product = variation.getProduct();

        productRepository.findByIdAndSellerIdAndIsDeletedFalse(
                product.getId(),
                user.getSeller().getId()
        ).orElseThrow(() -> new AccessDeniedException("You do not own this product"));

        if (!product.getIsActive()) {
            throw new InvalidRequestException("Product is not active");
        }

        boolean updated = false;

        if (request.getPrice() != null) {
            if (request.getPrice().equals(variation.getPrice())) {
                throw new InvalidRequestException("Price is same as existing");
            }
            variation.setPrice(request.getPrice());
            updated = true;
        }

        if (request.getQuantity() != null) {
            if (request.getQuantity().equals(variation.getQuantityAvailable())) {
                throw new InvalidRequestException("Quantity is same as existing");
            }
            variation.setQuantityAvailable(request.getQuantity());
            updated = true;
        }

        if (request.getIsActive() != null) {
            if (request.getIsActive().equals(variation.getIsActive())) {
                throw new InvalidRequestException("Active flag unchanged");
            }
            variation.setIsActive(request.getIsActive());
            updated = true;
        }

        if (request.getMetadata() != null) {

            String normalizedMetadata = validateAndNormalizeMetadata(
                    request.getMetadata(),
                    product.getCategory()
            );

            if (normalizedMetadata.equals(variation.getMetadata())) {
                throw new InvalidRequestException("Metadata is same as existing");
            }

            Optional<ProductVariation> duplicate =
                    variationRepository.findDuplicateVariation(product, normalizedMetadata);

            if (duplicate.isPresent() &&
                    !duplicate.get().getId().equals(variation.getId())) {
                throw new InvalidRequestException("Duplicate variation already exists");
            }

            variation.setMetadata(normalizedMetadata);
            updated = true;
        }

        if (request.getPrimaryImage() != null && !request.getPrimaryImage().isEmpty()) {

            String fileName = fileStorageService.storeProductVariationImage(
                    request.getPrimaryImage(),
                    product.getId(),
                    variation.getId(),
                    true,
                    0
            );

            variation.setPrimaryImageName(fileName);
            updated = true;
        }

        if (request.getSecondaryImages() != null && !request.getSecondaryImages().isEmpty()) {

            if (variation.getImages() != null) {
                variation.getImages().clear();
            }

            int index = 1;

            for (MultipartFile file : request.getSecondaryImages()) {

                String fileName = fileStorageService.storeProductVariationImage(
                        file,
                        product.getId(),
                        variation.getId(),
                        false,
                        index++
                );

                ProductVariationImage image = new ProductVariationImage();
                image.setImageName(fileName);
                image.setProductVariation(variation);
                image.setIsPrimary(false);

                variation.getImages().add(image);
            }

            updated = true;
        }

        if (!updated) {
            throw new InvalidRequestException("No changes detected");
        }

        variationRepository.save(variation);

        return "Product variation updated successfully";
    }

    private String validateAndNormalizeMetadata(String metadataJson, Category category) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            Map<String, String> inputMap = mapper.readValue(metadataJson, Map.class);

            if (inputMap.isEmpty()) {
                throw new InvalidRequestException("Metadata cannot be empty");
            }

            List<CategoryMetadataFieldValues> allowedValues =
                    category.getCategoryMetadataFieldValues();

            Map<String, Set<String>> allowedMap = new HashMap<>();

            for (CategoryMetadataFieldValues val : allowedValues) {
                String field = val.getMetadataField().getName().toLowerCase();
                String value = val.getValue().toLowerCase();

                allowedMap
                        .computeIfAbsent(field, k -> new HashSet<>())
                        .add(value);
            }

            Map<String, String> normalizedMap = new TreeMap<>();

            for (Map.Entry<String, String> entry : inputMap.entrySet()) {

                String key = entry.getKey().toLowerCase();
                String value = entry.getValue().toLowerCase();

                if (!allowedMap.containsKey(key)) {
                    throw new InvalidRequestException("Invalid metadata field: " + key);
                }

                if (!allowedMap.get(key).contains(value)) {
                    throw new InvalidRequestException(
                            "Invalid value '" + value + "' for field '" + key + "'"
                    );
                }

                normalizedMap.put(key, value);
            }

            if (normalizedMap.size() != allowedMap.size()) {
                throw new InvalidRequestException("All metadata fields must be provided");
            }

            return mapper.writeValueAsString(normalizedMap);

        } catch (Exception e) {
            throw new InvalidRequestException("Invalid metadata format");
        }
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
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
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