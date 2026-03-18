package org.project.ttnecommerce.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.ttnecommerce.dto.*;
import org.project.ttnecommerce.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerSeller(@Valid @RequestBody RegisterSellerRequest request) {
        log.info("Seller API called: Register seller | email={}", request.getEmail());
        sellerService.registerSeller(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Seller registered successfully. Waiting for approval."));
    }

    @GetMapping("/profile")
    public ResponseEntity<SellerProfileResponse> getSellerProfile() {
        log.info("Seller API called: Get seller profile");
        SellerProfileResponse response = sellerService.getSellerProfile();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<String> updateSellerProfile(@RequestBody SellerProfileUpdateRequest request) {
        log.info("Seller API called: Update seller profile");
        sellerService.updateSellerProfile(request);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @PatchMapping("/change-password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        log.info("Seller API called: Change seller password");
        sellerService.updateSellerPassword(request);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PatchMapping("/change-address/{addressId}")
    public ResponseEntity<String> updateAddress(@PathVariable UUID addressId, @Valid @RequestBody UpdateAddressRequest request) {
        log.info("Seller API called: Update address | addressId={}", addressId);
        sellerService.updateAddress(addressId, request);
        return ResponseEntity.ok("Address updated successfully");
    }

    @PostMapping("/profile/image")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("file") MultipartFile file) {
        log.info("Seller API called: Upload profile image");
        sellerService.uploadProfileImage(file);
        return ResponseEntity.ok("Profile image uploaded successfully");
    }

    @GetMapping("/get-categories")
    public ResponseEntity<List<SellerCategoryResponse>> getCategory() {
        log.info("Seller API called: Get seller categories");
        List<SellerCategoryResponse> response = sellerService.getCategory();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-products")
    public ResponseEntity<String> addProduct(@Valid @RequestBody AddProductRequest request) {
        String response = sellerService.addProduct(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping(value = "/add-product-variation", consumes = "multipart/form-data")
    public ResponseEntity<String> addProductVariation(@ModelAttribute @Valid AddProductVariationRequest request) {
        String response = sellerService.addProductVariation(request);
        return ResponseEntity.ok(response);
    }


}

