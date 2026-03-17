package org.project.ttnecommerce.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAddressRequest {

    @Size(min = 3, max = 255, message = "Address line must be between 3 and 255 characters")
    private String addressLine;

    @Pattern(regexp = "^[A-Za-z][A-Za-z ]{1,49}$",
            message = "City must contain only letters and spaces")
    private String city;

    @Pattern(regexp = "^[A-Za-z][A-Za-z ]{1,49}$",
            message = "State must contain only letters and spaces")
    private String state;

    @Pattern(regexp = "^[A-Za-z][A-Za-z ]{1,49}$",
            message = "Country must contain only letters and spaces")
    private String country;

    @Pattern(regexp = "^[0-9]{6}$",
            message = "Zip code must be exactly 6 digits")
    private String zipCode;

    @Size(min = 2, max = 50, message = "Label must be between 2 and 50 characters")
    private String label;
}