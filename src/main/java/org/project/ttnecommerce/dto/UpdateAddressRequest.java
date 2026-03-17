package org.project.ttnecommerce.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAddressRequest {

    @Pattern(
            regexp = "^[A-Za-z0-9 ,./#-]*$",
            message = "Address contains invalid characters"
    )
    private String addressLine;

    @Pattern(
            regexp = "^(?!\\s*$)[A-Za-z]+(?: [A-Za-z]+)*$",
            message = "City must contain only alphabets and single spaces"
    )
    private String city;

    @Pattern(
            regexp = "^(?!\\s*$)[A-Za-z]+(?: [A-Za-z]+)*$",
            message = "State must contain only alphabets and single spaces"
    )
    private String state;

    @Pattern(
            regexp = "^(?!\\s*$)[A-Za-z]+(?: [A-Za-z]+)*$",
            message = "Country must contain only alphabets and single spaces"
    )
    private String country;

    @Pattern(
            regexp = "^(?!\\s*$)[1-9][0-9]{5}$",
            message = "Zip code must be 6 digits and cannot start with 0"
    )
    private String zipCode;

    @Pattern(
            regexp = "^(?!\\s*$)[A-Za-z ]{2,50}$",
            message = "Label must contain only alphabets and be between 2 and 50 characters"
    )
    private String label;
}