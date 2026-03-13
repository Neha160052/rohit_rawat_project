package org.project.ttnecommerce.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.*;
import org.project.ttnecommerce.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerSeller(
            @Valid @RequestBody RegisterSellerRequest request) {
        sellerService.registerSeller(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse("Seller registered successfully. Waiting for approval."));
    }


    @GetMapping("/profile")
    public ResponseEntity<SellerProfileResponse> getSellerProfile() {
        SellerProfileResponse response = sellerService.getSellerProfile();
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/update-profile")
    public ResponseEntity<String> updateSellerProfile(
            @RequestBody SellerProfileUpdateRequest request) {
        sellerService.updateSellerProfile(request);
        return ResponseEntity.ok("Profile updated successfully");
    }


    @PatchMapping("/change-password")
    public ResponseEntity<String> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {

        sellerService.updateSellerPassword(request);

        return ResponseEntity.ok("Password updated successfully");
    }

    @PatchMapping("/change-address/{addressId}")
    public ResponseEntity<String> updateAddress(

            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateAddressRequest request) {

        sellerService.updateAddress(addressId, request);

        return ResponseEntity.ok("Address updated successfully");
    }

    @PostMapping("/profile/image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        sellerService.uploadProfileImage(file);
        return ResponseEntity.ok("Profile image uploaded successfully");
    }
}
