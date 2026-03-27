package org.project.ttnecommerce.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.ttnecommerce.dto.*;
import org.project.ttnecommerce.i18n.MessageTranslator;
import org.project.ttnecommerce.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final MessageTranslator translator;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        log.info("Customer API called: Register customer | email={}", request.getEmail());
        customerService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(translator.get("response.customer.registered")));
    }

    @PutMapping("/activate-customer")
    public ResponseEntity<ApiResponse> activateCustomer(@RequestParam String token) {
        log.info("Customer API called: Activate customer account");
        customerService.activateCustomer(token);
        return ResponseEntity.ok(new ApiResponse(translator.get("response.customer.activated")));
    }

    @PostMapping("/resend-activation-link")
    public ResponseEntity<ApiResponse> resendActivationLink(@Valid @RequestBody ResendActivationRequest request) {
        log.info("Customer API called: Resend activation link | email={}", request.getEmail());
        customerService.resendActivationLink(request.getEmail());
        return ResponseEntity.ok(new ApiResponse(translator.get("response.activation.link.sent")));
    }

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileResponse> viewCustomerProfile() {
        log.info("Customer API called: View customer profile");
        CustomerProfileResponse response = customerService.getCustomerProfile();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<ApiResponse> updateCustomerProfile(@Valid @RequestBody UpdateCustomerProfileRequest request) {
        log.info("Customer API called: Update profile");
        customerService.updateCustomerProfile(request);
        return ResponseEntity.ok(new ApiResponse(translator.get("response.profile.updated")));
    }

    @PostMapping("/add-address")
    public ResponseEntity<String> addAddress(@Valid @RequestBody AddAddressRequest request) {
        log.info("Customer API called: Add address");
        customerService.addAddress(request);
        return ResponseEntity.ok(translator.get("response.address.added"));
    }

    @GetMapping("/get-addresses")
    public ResponseEntity<List<AddAddressResponse>> getMyAddresses() {
        log.info("Customer API called: Get customer addresses");
        return ResponseEntity.ok(customerService.getMyAddresses());
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        log.info("Customer API called: Change password");
        customerService.changePassword(request);
        return ResponseEntity.ok(new ApiResponse(translator.get("response.password.updated")));
    }

    @DeleteMapping("/delete-address/{addressId}")
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable UUID addressId) {
        log.info("Customer API called: Delete address | addressId={}", addressId);
        customerService.deleteAddress(addressId);
        return ResponseEntity.ok(new ApiResponse(translator.get("response.address.deleted")));
    }

    @PatchMapping("/update-address/{addressId}")
    public ResponseEntity<ApiResponse> updateAddress(@PathVariable UUID addressId, @Valid @RequestBody UpdateAddressRequest request) {
        log.info("Customer API called: Update address | addressId={}", addressId);
        customerService.updateAddress(addressId, request);
        return ResponseEntity.ok(new ApiResponse(translator.get("response.address.updated")));
    }

    @PostMapping("/profile/image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        log.info("Customer API called: Upload profile image");
        customerService.uploadProfileImage(file);
        return ResponseEntity.ok(translator.get("response.profile.image.uploaded"));
    }

    @GetMapping("/get-categories")
    public ResponseEntity<List<CustomerCategoryResponse>> getCategories(@RequestParam(required = false) UUID categoryId) {
        log.info("Customer API called: Get categories | categoryId={}", categoryId);
        return ResponseEntity.ok(customerService.getCategories(categoryId));
    }

    @GetMapping("/get-category/filter-details")
    public ResponseEntity<CategoryFilterResponse> getCategoryFilterDetails(@RequestParam UUID categoryId) {
        log.info("Customer API called: Get category filter details | categoryId={}", categoryId);
        CategoryFilterResponse response = customerService.getCategoryFilterDetails(categoryId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/get-products/{productId}")
    public ResponseEntity<ProductResponse> viewProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(customerService.viewProduct(productId));
    }

    @GetMapping("/getAll-category-products")
    public ResponseEntity<List<ProductResponse>> viewAllProducts(
            @RequestParam UUID categoryId,
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order
    ) {
        return ResponseEntity.ok(
                customerService.viewAllProducts(categoryId, max, offset, sort, order)
        );
    }

    @GetMapping("/get-products/similar/{productId}")
    public ResponseEntity<List<SimilarProductResponse>> getSimilarProducts(
            @PathVariable UUID productId,
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order
    ) {
        return ResponseEntity.ok(
                customerService.getSimilarProducts(productId, max, offset, sort, order)
        );
    }
}
