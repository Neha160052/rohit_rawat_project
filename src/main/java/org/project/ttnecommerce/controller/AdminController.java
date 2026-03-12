package org.project.ttnecommerce.controller;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.AdminCustomerResponse;
import org.project.ttnecommerce.dto.AdminSellerResponse;
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
}