package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddAddressRequest {

    @NotBlank(message = "{validation.address.empty}")
    @Size(min = 2, max = 255, message = "{validation.address.size}")
    private String addressLine;

    @NotBlank(message = "{validation.city.empty}")
    @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "{validation.city.alpha}")
    private String city;

    @NotBlank(message = "{validation.state.empty}")
    @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "{validation.state.alpha}")
    private String state;

    @NotBlank(message = "{validation.country.empty}")
    @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "{validation.country.alpha}")
    private String country;

    @NotBlank(message = "{validation.zip.empty}")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "{validation.zip.invalid}")
    private String zipCode;

    @NotBlank(message = "{validation.label.empty}")
    @Pattern(regexp = "^(HOME|WORK|OTHER)$", message = "{validation.label.invalid.enum}")
    private String label;
}
