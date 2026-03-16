package org.project.ttnecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddAddressRequest {
    @NotBlank(message = "Address cannot be empty")
    private String addressLine;

    @NotBlank(message = "City cannot be empty")
    private String city;

    @NotBlank(message = "State cannot be empty")
    private String state;

    @NotBlank(message = "Country cannot be empty")
    private String country;

    @NotBlank(message = "Zip code cannot be empty")
    @Pattern(regexp = "^[0-9]{5,6}$", message = "Invalid zip code")
    private String zipCode;

    @NotBlank(message = "Label cannot be empty")
    private String label;
}