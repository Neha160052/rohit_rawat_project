package org.project.ttnecommerce.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.project.ttnecommerce.dto.ApiResponse;
import org.project.ttnecommerce.dto.RegisterCustomerRequest;
import org.project.ttnecommerce.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private CustomerService customerService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerCustomer(
            @Valid @RequestBody RegisterCustomerRequest request) {

        customerService.registerCustomer(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Customer registered successfully"));
    }

}
