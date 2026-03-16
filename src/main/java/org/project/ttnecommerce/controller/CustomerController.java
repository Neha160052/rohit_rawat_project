package org.project.ttnecommerce.controller;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.project.ttnecommerce.dto.*;
import org.project.ttnecommerce.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        customerService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Customer registered successfully"));
    }

    @PutMapping("/activate-customer")
    public ResponseEntity<ApiResponse> activateCustomer(@RequestParam String token) {
        customerService.activateCustomer(token);
        return ResponseEntity.ok(
                new ApiResponse("Customer account activated successfully")
        );
    }

    @PostMapping("/resend-activation-link")
    public ResponseEntity<ApiResponse> resendActivationLink(@Valid @RequestBody ResendActivationRequest request) {
        customerService.resendActivationLink(request.getEmail());
        return ResponseEntity.ok(
                new ApiResponse("Activation link sent successfully")
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileResponse> viewCustomerProfile() {
        CustomerProfileResponse response = customerService.getCustomerProfile();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<ApiResponse> updateCustomerProfile(
            @Valid @RequestBody UpdateCustomerProfileRequest request) {
        customerService.updateCustomerProfile(request);
        return ResponseEntity.ok(new ApiResponse("Profile updated successfully"));
    }


    @PostMapping("/add-address")
    public ResponseEntity<String> addAddress(
            @Valid @RequestBody AddAddressRequest request) {
        customerService.addAddress(request);
        return ResponseEntity.ok("Address added successfully");
    }

    @GetMapping("/get-addresses")
    public ResponseEntity<java.util.List<AddAddressResponse>> getMyAddresses() {
        return ResponseEntity.ok(customerService.getMyAddresses());
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {

        customerService.changePassword(request);

        return ResponseEntity.ok(
                new ApiResponse("Password updated successfully")
        );
    }


    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<ApiResponse> deleteAddress(
            @PathVariable UUID addressId) {

        customerService.deleteAddress(addressId);

        return ResponseEntity.ok(
                new ApiResponse("Address deleted successfully")
        );
    }

    @PatchMapping("/address/{addressId}")
    public ResponseEntity<ApiResponse> updateAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateAddressRequest request) {

        customerService.updateAddress(addressId, request);

        return ResponseEntity.ok(
                new ApiResponse("Address updated successfully")
        );
    }


    @PostMapping("/profile/image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        customerService.uploadProfileImage(file);
        return ResponseEntity.ok("Profile image uploaded successfully");
    }


    @GetMapping("/get-categories")
    public ResponseEntity<List<CustomerCategoryResponse>> getCategories(@RequestParam(required = false) UUID categoryId) {
        return ResponseEntity.ok(customerService.getCategories(categoryId));
    }






}