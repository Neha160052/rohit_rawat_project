package org.project.ttnecommerce.controller;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.project.ttnecommerce.dto.*;
import org.project.ttnecommerce.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<CustomerProfileResponse> viewMyProfile() {
        CustomerProfileResponse response = customerService.getCustomerProfile();
        return ResponseEntity.ok(response);
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

    @PostMapping("/profile/image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        customerService.uploadProfileImage(file);
        return ResponseEntity.ok("Profile image uploaded successfully");
    }

}
