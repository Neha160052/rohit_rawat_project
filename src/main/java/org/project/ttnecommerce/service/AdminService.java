package org.project.ttnecommerce.service;

import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.*;
import org.project.ttnecommerce.entity.*;
import org.project.ttnecommerce.exception.InvalidInputException;
import org.project.ttnecommerce.exception.InvalidRequestException;
import org.project.ttnecommerce.repository.CategoryMetadataFieldRepository;
import org.project.ttnecommerce.repository.CategoryMetadataFieldValuesRepository;
import org.project.ttnecommerce.repository.CategoryRepository;
import org.project.ttnecommerce.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final CategoryMetadataFieldRepository metadataRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "email", "firstName", "lastName");

    private static final Set<String> CATEGORY_SORT_FIELDS = Set.of("id", "name");


    // getAll Customer method
    public List<AdminCustomerResponse> getAllCustomers(int pageOffset, int pageSize, String sort, String email) {
        validatePagination(pageOffset, pageSize);
        validateSort(sort);
        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by(sort).ascending());
        Page<User> customers;
        if (email != null && !email.isBlank()) {
            customers = userRepository.findByCustomerIsNotNullAndEmailContainingIgnoreCaseAndIsDeletedFalse(email, pageable);
        }
        else {
            customers = userRepository.findByCustomerIsNotNullAndIsDeletedFalse(pageable);
        }
        return customers.map(this::convertCustomerToDTO).getContent();
    }

    // getAll Seller method
    public List<AdminSellerResponse> getAllSellers(int pageOffset, int pageSize, String sort, String email) {
        validatePagination(pageOffset, pageSize);
        validateSort(sort);
        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by(sort).ascending());
        Page<User> sellers;
        if (email != null && !email.isBlank()) {
            sellers = userRepository.findBySellerIsNotNullAndEmailContainingIgnoreCaseAndIsDeletedFalse(email, pageable);
        }
        else {
            sellers = userRepository.findBySellerIsNotNullAndIsDeletedFalse(pageable);
        }
        return sellers.map(this::convertSellerToDTO).getContent();
    }

    // activateCustomer method
    @Transactional
    public String activateCustomer(UUID userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        if (user.getIsDeleted()) throw new InvalidRequestException("Deleted User Cannot be Activated");

        if (user.getCustomer() == null) throw new InvalidRequestException("User is not a customer");

        if (user.getIsActive()) return "Customer already active";

        user.setIsActive(true);
        userRepository.save(user);
        emailService.sendCustomerActivationEmailByAdmin(user);
        return "Customer activated successfully";
    }

    // deactivateCustomer method
    @Transactional
    public String deactivateCustomer(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));

        if (user.getIsDeleted())
            throw new InvalidRequestException("Deleted Customer Cannot be Deactivated");

        if (user.getCustomer() == null)
            throw new InvalidRequestException("User is not a customer");

        if (!user.getIsActive())
            return "Customer already deactivated";

        user.setIsActive(false);
        userRepository.save(user);

        emailService.sendCustomerDeactivationEmailByAdmin(user);

        return "Customer deactivated successfully";
    }

    // activate Seller method
    @Transactional
    public String activateSeller(UUID userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        if (user.getIsDeleted())
            throw new InvalidRequestException("Deleted Seller Cannot be Activated");

        Seller seller = user.getSeller();
        if (seller == null)
            throw new InvalidRequestException("User is not a seller");

        if (user.getIsActive() && seller.getIsApproved())
            return "Seller already active";

        user.setIsActive(true);
        seller.setIsApproved(true);
        emailService.sendSellerActivationEmailByAdmin(user);
        return "Seller activated successfully";
    }
    // Deactivate Seller method

    @Transactional
    public String deactivateSeller(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (user.getIsDeleted())
            throw new InvalidRequestException("Deleted Seller Cannot be Deactivated");

        Seller seller = user.getSeller();
        if (user.getSeller() == null)
            throw new InvalidRequestException("User is not a seller");

        if (!user.getIsActive())
            return "Seller already deactivated";

        user.setIsActive(false);
        seller.setIsApproved(false);
        userRepository.save(user);
        emailService.sendSellerDeactivationEmailByAdmin(user);
        return "Seller deactivated successfully";
    }

    // Add-Metadata field method
    @Transactional
    public String addMetadataField(AddMetadataFieldRequest request) {
        String fieldName = request.getName().trim().toLowerCase();
        if (fieldName.isBlank())
            throw new InvalidInputException("Field name cannot be empty");

        metadataRepository.findByNameIgnoreCaseAndIsDeletedFalse(fieldName).ifPresent(field -> {
                    throw new InvalidRequestException("Metadata field already exists");
                });

        CategoryMetadataField field = CategoryMetadataField.builder()
                .name(fieldName)
                .isDeleted(false)
                .build();
        metadataRepository.save(field);
        return "Metadata field created successfully with ID: " + field.getId();
    }

    // get metafield method

    public List<MetadataFieldResponse> getAllMetadataFields(Integer max, Integer offset, String sort, String order, String query) {

        if (max == null || max <= 0) max = 10;
        if (offset == null || offset < 0) offset = 0;
        if (sort == null) sort = "name";

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));
        Page<CategoryMetadataField> fields;
        if (query != null && !query.trim().isEmpty()) {
            fields = metadataRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(query.trim(), pageable);
        }
        else {
            fields = metadataRepository.findByIsDeletedFalse(pageable);
        }

        return fields.stream()
                .map(field -> new MetadataFieldResponse(field.getId(), field.getName()))
                .toList();
    }

    // add Category method

    @Transactional
    public String addCategory(AddCategoryRequest request) {
        String name = request.getName().trim();
        if (name.isEmpty())
            throw new InvalidInputException("Category name cannot be empty");

        Category parent = null;

        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId()).orElseThrow(() -> new InvalidRequestException("Parent category not found"));

            if (parent.getIsDeleted())
                throw new InvalidRequestException("Parent category is deleted");

            categoryRepository.findByNameIgnoreCaseAndParentCategoryIdAndIsDeletedFalse(name, parent.getId()).ifPresent(c -> {
                        throw new InvalidRequestException("Category already exists under this parent");
                    });

        }
        else {
            categoryRepository.findByNameIgnoreCaseAndParentCategoryIsNullAndIsDeletedFalse(name).ifPresent(c -> {
                        throw new InvalidRequestException("Root category already exists");
                    });
        }

        Category category = Category.builder()
                .name(name)
                .parentCategory(parent)
                .isDeleted(false)
                .build();

        categoryRepository.save(category);
        return "Category created successfully with ID: " + category.getId();
    }

    // getAll categories method

    public List<CategoryResponse> getAllCategories(int max, int offset, String sort, String order, String query, UUID categoryId) {
        if (max <= 0 || max > 100)
            throw new InvalidInputException("max must be between 1 and 100");

        if (offset < 0)
            throw new InvalidInputException("offset cannot be negative");

        if (!CATEGORY_SORT_FIELDS.contains(sort))
            throw new InvalidInputException("Invalid sort field");

        if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))
            throw new InvalidInputException("order must be asc or desc");

        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(offset, max, Sort.by(direction, sort));
        Page<Category> page;

        if (query != null && !query.isBlank()) {
            page = categoryRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(query, pageable);
        }
        else {
            page = categoryRepository.findByIsDeletedFalse(pageable);
        }
        return page.getContent()
                .stream()
                .map(this::mapToCategoryResponse)
                .toList();
    }

    // map category response

    private CategoryResponse mapToCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setParentCategories(buildParentHierarchy(category));
        response.setChildren(mapChildren(category));
        response.setMetadataFields(mapMetadata(category));
        return response;
    }

    private List<ParentCategoryDto> buildParentHierarchy(Category category) {
        List<ParentCategoryDto> parents = new ArrayList<>();
        Category parent = category.getParentCategory();
        while (parent != null) {
            ParentCategoryDto dto = new ParentCategoryDto();
            dto.setId(parent.getId());
            dto.setName(parent.getName());
            parents.add(dto);
            parent = parent.getParentCategory();
        }
        return parents;
    }

    private List<ChildCategoryDto> mapChildren(Category category) {
        return category.getChildren()
                .stream()
                .filter(child -> !child.getIsDeleted())
                .map(child -> {
                    ChildCategoryDto dto = new ChildCategoryDto();
                    dto.setId(child.getId());
                    dto.setName(child.getName());
                    return dto;
                })
                .toList();
    }

    private List<CategoryMetadataFieldResponse> mapMetadata(Category category) {

        return category.getCategoryMetadataFieldValues()
                .stream()
                .map(value -> {
                    CategoryMetadataFieldResponse dto = new CategoryMetadataFieldResponse();
                    dto.setFieldName(value.getMetadataField().getName());
                    dto.setPossibleValues(Arrays.asList(value.getValue().split(",")));
                    return dto;

                })
                .toList();
    }


    // update category method
    @Transactional
    public String updateCategory(UpdateCategoryRequest request) {

        UUID id = request.getId();
        String name = request.getName().trim();

        if (id == null) {
            throw new InvalidInputException("Category id cannot be null");
        }

        if (name.isEmpty()) {
            throw new InvalidInputException("Category name cannot be empty");
        }

        Category category = categoryRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new InvalidRequestException("Category not found"));

        Category parent = category.getParentCategory();

        if (parent == null) {

            categoryRepository
                    .findByNameIgnoreCaseAndParentCategoryIsNullAndIsDeletedFalse(name)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new InvalidRequestException("Root category already exists");
                        }
                    });

        } else {

            categoryRepository
                    .findByNameIgnoreCaseAndParentCategoryIdAndIsDeletedFalse(name, parent.getId())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new InvalidRequestException("Category already exists under this parent");
                        }
                    });
        }

        category.setName(name);

        categoryRepository.save(category);

        return "Category updated successfully";
    }



    // add category metadata method
    @Transactional
    public String addCategoryMetadata(AddCategoryMetadataRequest request) {

        if (request.getCategoryId() == null) {
            throw new InvalidInputException("Category id is required");
        }

        Category category = categoryRepository
                .findByIdAndIsDeletedFalse(request.getCategoryId())
                .orElseThrow(() -> new InvalidRequestException("Category not found"));

        if (request.getMetadata() == null || request.getMetadata().isEmpty()) {
            throw new InvalidInputException("Metadata list cannot be empty");
        }

        Set<UUID> usedFieldIds = new HashSet<>();

        for (MetadataFieldValuesRequest meta : request.getMetadata()) {

            if (meta.getFieldId() == null) {
                throw new InvalidInputException("Metadata field id is required");
            }

            if (!usedFieldIds.add(meta.getFieldId())) {
                throw new InvalidRequestException("Duplicate metadata field in request");
            }

            CategoryMetadataField field = metadataRepository.findByIdAndIsDeletedFalse(meta.getFieldId())
                    .orElseThrow(() -> new InvalidRequestException("Metadata field not found"));

            boolean exists = categoryMetadataFieldValuesRepository.existsByCategoryAndMetadataField(category, field);

            if (exists) {
                throw new InvalidRequestException(
                        "Metadata field already assigned to this category");
            }

            if (meta.getValues() == null || meta.getValues().isEmpty()) {
                throw new InvalidInputException("At least one value must be provided");
            }

            Set<String> uniqueValues = new HashSet<>();

            for (String value : meta.getValues()) {

                if (value == null || value.trim().isEmpty()) {
                    throw new InvalidInputException("Metadata value cannot be empty");
                }

                String normalizedValue = value.trim().toLowerCase();

                if (!uniqueValues.add(normalizedValue)) {
                    throw new InvalidInputException("Duplicate metadata values not allowed");
                }
            }
            String joinedValues = String.join(",", uniqueValues);
            CategoryMetadataFieldValues entity =
                    CategoryMetadataFieldValues.builder()
                            .category(category)
                            .metadataField(field)
                            .value(joinedValues)
                            .build();

            categoryMetadataFieldValuesRepository.save(entity);
        }
        return "Metadata fields added successfully";
    }

















    // ================= UTIL METHODS =================

    private void validatePagination(int pageOffset, int pageSize) {

        if (pageOffset < 0)
            throw new InvalidRequestException("Page offset cannot be negative");

        if (pageSize <= 0)
            throw new InvalidRequestException("Page size must be greater than zero");

        if (pageSize > 50)
            throw new InvalidRequestException("Page size cannot exceed 50");
    }

    private void validateSort(String sort) {

        if (!ALLOWED_SORT_FIELDS.contains(sort))
            throw new InvalidRequestException("Invalid sort field: " + sort);
    }

    private AdminCustomerResponse convertCustomerToDTO(User user) {

        String fullName = user.getFirstName() + " "
                + (user.getMiddleName() != null ? user.getMiddleName() + " " : "")
                + user.getLastName();

        return new AdminCustomerResponse(
                user.getId(),
                fullName,
                user.getEmail(),
                user.getIsActive()
        );
    }

    private AdminSellerResponse convertSellerToDTO(User user) {

        Seller seller = user.getSeller();

        String fullName = user.getFirstName() + " "
                + (user.getMiddleName() != null ? user.getMiddleName() + " " : "")
                + user.getLastName();

        Address address = user.getAddresses().stream().findFirst().orElse(null);

        String companyAddress = null;

        if (address != null) {

            companyAddress = address.getAddressLine() + ", "
                    + address.getCity() + ", "
                    + address.getState() + ", "
                    + address.getCountry();
        }

        return new AdminSellerResponse(
                user.getId(),
                fullName,
                user.getEmail(),
                user.getIsActive(),
                seller.getCompanyName(),
                companyAddress,
                seller.getCompanyContact()
        );
    }
}