package org.project.ttnecommerce.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerProfileRequest {

    @Pattern(regexp = "^[A-Za-z]{2,50}$", message = "First name must be 2-50 alphabets")
    private String firstName;

    @Pattern(regexp = "^[A-Za-z]{2,50}$", message = "Last name must be 2-50 alphabets")
    private String lastName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be a valid 10 digit number")
    private String contact;
}