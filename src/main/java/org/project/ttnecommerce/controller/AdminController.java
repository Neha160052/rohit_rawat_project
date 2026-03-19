package org.project.ttnecommerce.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.ttnecommerce.dto.*;
import org.project.ttnecommerce.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/customers")
    public ResponseEntity<List<AdminCustomerResponse>> getAllCustomers(
            @RequestParam(defaultValue = "0") int pageOffset,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String email
    ) {

        log.info("Admin API called: Fetch customers | offset={} size={} sort={} email={}",
                pageOffset, pageSize, sort, email);

        List<AdminCustomerResponse> customers =
                adminService.getAllCustomers(pageOffset, pageSize, sort, email);

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<AdminSellerResponse>> getAllSellers(
            @RequestParam(defaultValue = "0") int pageOffset,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String email
    ) {

        log.info("Admin API called: Fetch sellers | offset={} size={} sort={} email={}",
                pageOffset, pageSize, sort, email);
        List<AdminSellerResponse> sellers = adminService.getAllSellers(pageOffset, pageSize, sort, email);
        return ResponseEntity.ok(sellers);
    }

    @PatchMapping("/customers/{id}")
    public ResponseEntity<MessageResponse> updateCustomerStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest request) {

        return ResponseEntity.ok(adminService.updateCustomerStatus(id, request.getStatus()));
    }

    @PatchMapping("/sellers/{id}")
    public ResponseEntity<MessageResponse> updateSellerStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest request) {

        return ResponseEntity.ok(
                adminService.updateSellerStatus(id, request.getStatus())
        );
    }

    @PostMapping("/add-metadata-field")
    public ResponseEntity<ApiResponse> addMetadataField(@Valid @RequestBody AddMetadataFieldRequest request) {
        log.info("Admin API called: Add metadata field | name={}", request.getName());
        String message = adminService.addMetadataField(request);
        return ResponseEntity.ok(new ApiResponse(message));
    }

    @GetMapping("/get-metadata-field")
    public ResponseEntity<List<MetadataFieldResponse>> getAllMetadataFields(
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String query) {

        log.info("Admin API called: Fetch metadata fields | max={} offset={} sort={} order={} query={}", max, offset, sort, order, query);
        List<MetadataFieldResponse> response = adminService.getAllMetadataFields(max, offset, sort, order, query);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-category")
    public ResponseEntity<ApiResponse> addCategory(@Valid @RequestBody AddCategoryRequest request) {
        log.info("Admin API called: Add category | name={} parentId={}",
                request.getName(), request.getParentId());
        String message = adminService.addCategory(request);
        return ResponseEntity.ok(new ApiResponse(message));
    }

    @GetMapping("/get-categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "10") int max,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UUID categoryId
    ) {

        log.info("Admin API called: Fetch categories | max={} offset={} sort={} order={} query={} categoryId={}",
                max, offset, sort, order, query, categoryId);
        List<CategoryResponse> response = adminService.getAllCategories(max, offset, sort, order, query, categoryId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-category")
    public ResponseEntity<ApiResponse> updateCategory(@RequestBody UpdateCategoryRequest request) {
        log.info("Admin API called: Update category | categoryId={}", request.getId());
        String message = adminService.updateCategory(request);
        return ResponseEntity.ok(new ApiResponse(message));
    }

    @PostMapping("/add-category/metadata")
    public ResponseEntity<ApiResponse> addCategoryMetadata(
            @RequestBody AddCategoryMetadataRequest request) {
        log.info("Admin API called: Add category metadata | categoryId={}", request.getCategoryId());
        String message = adminService.addCategoryMetadata(request);
        return ResponseEntity.ok(new ApiResponse(message));
    }

    @GetMapping("/get-products")
    public ResponseEntity<ProductListResponse> getAllProducts(
            @RequestParam(defaultValue = "10") Integer max,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) UUID sellerId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID productId
    ) {

        log.info("Admin: Fetch products | max={} offset={} sort={} order={} sellerId={} categoryId={} productId={}",
                max, offset, sort, order, sellerId, categoryId, productId);

        return ResponseEntity.ok(
                adminService.getAllProducts(max, offset, sort, order, sellerId, categoryId, productId)
        );
    }

    @PutMapping("/product-status")
    public ResponseEntity<String> updateProductStatus(@Valid @RequestBody ProductStatusUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateProductStatus(request));
    }
}

