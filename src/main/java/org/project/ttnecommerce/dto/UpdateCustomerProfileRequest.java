package org.project.ttnecommerce.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerProfileRequest {

    @Pattern(regexp = "^[A-Za-z]{2,50}$", message = "{validation.first.name.range}")
    private String firstName;

    @Pattern(regexp = "^[A-Za-z]{2,50}$", message = "{validation.last.name.range}")
    private String lastName;

    @Pattern(regexp = "^[0-9]{10}$", message = "{validation.phone.valid}")
    private String contact;
}
