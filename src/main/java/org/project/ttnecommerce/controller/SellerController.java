package org.project.ttnecommerce.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.ApiResponse;
import org.project.ttnecommerce.dto.RegisterSellerRequest;
import org.project.ttnecommerce.dto.SellerProfileResponse;
import org.project.ttnecommerce.dto.SellerProfileUpdateRequest;
import org.project.ttnecommerce.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    @PostMapping("/profile/image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        sellerService.uploadProfileImage(file);
        return ResponseEntity.ok("Profile image uploaded successfully");
    }
}
