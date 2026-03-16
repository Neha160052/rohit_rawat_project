package org.project.ttnecommerce.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.*;
import org.project.ttnecommerce.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

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

        List<AdminCustomerResponse> customers = adminService.getAllCustomers(pageOffset, pageSize, sort, email);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<AdminSellerResponse>> getAllSellers(
            @RequestParam(defaultValue = "0") int pageOffset,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String email
    ) {
        List<AdminSellerResponse> sellers =  adminService.getAllSellers(pageOffset, pageSize, sort, email);
        return ResponseEntity.ok(sellers);
    }

    @PatchMapping("/customers/activate/{id}")
    public ResponseEntity<String> activateCustomer(@PathVariable UUID id) {
        String response = adminService.activateCustomer(id);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/customers/deactivate/{id}")
    public ResponseEntity<String> deactivateCustomer(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.deactivateCustomer(id));
    }

    @PatchMapping("/sellers/activate/{id}")
    public ResponseEntity<String> activateSeller(@PathVariable UUID id) {
        String response = adminService.activateSeller(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/sellers/deactivate/{id}")
    public ResponseEntity<String> deactivateSeller(@PathVariable UUID id) {
        String response = adminService.deactivateSeller(id);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/add-metadata-field")
    public ResponseEntity<ApiResponse> addMetadataField(
            @Valid @RequestBody AddMetadataFieldRequest request) {
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

        List<MetadataFieldResponse> response = adminService.getAllMetadataFields(max, offset, sort, order, query);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/add-category")
    public ResponseEntity<ApiResponse> addCategory(@Valid @RequestBody AddCategoryRequest request) {
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

        List<CategoryResponse> response = adminService.getAllCategories(max, offset, sort, order, query, categoryId);
        return ResponseEntity.ok(response);
    }



    @PutMapping("/update-category")
    public ResponseEntity<ApiResponse> updateCategory(@RequestBody UpdateCategoryRequest request) {
        String message = adminService.updateCategory(request);
        return ResponseEntity.ok(new ApiResponse(message));
    }


    @PostMapping("/add-category/metadata")
    public ResponseEntity<ApiResponse> addCategoryMetadata(@RequestBody AddCategoryMetadataRequest request) {
        String message = adminService.addCategoryMetadata(request);
        return ResponseEntity.ok(new ApiResponse(message));
    }



}