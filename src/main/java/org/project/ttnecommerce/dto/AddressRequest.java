package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

    @NotBlank(message = "{validation.city.blank}")
    @Size(min = 2, max = 50, message = "{validation.city.size}")
    @Pattern(regexp = "^[A-Za-z]+(?:[ ][A-Za-z]+)*$", message = "{validation.city.alpha.single_space}")
    private String city;

    @NotBlank(message = "{validation.state.blank}")
    @Size(min = 2, max = 50, message = "{validation.state.size}")
    @Pattern(regexp = "^[A-Za-z]+(?:[ ][A-Za-z]+)*$", message = "{validation.state.alpha.single_space}")
    private String state;

    @NotBlank(message = "{validation.country.blank}")
    @Size(min = 2, max = 50, message = "{validation.country.size}")
    @Pattern(regexp = "^[A-Za-z]+(?:[ ][A-Za-z]+)*$", message = "{validation.country.alpha.single_space}")
    private String country;

    @NotBlank(message = "{validation.address.blank}")
    @Size(min = 2, max = 255, message = "{validation.address.size}")
    @Pattern(
            regexp = "^[A-Za-z0-9 ,./#-]+$",
            message = "{validation.address.invalid}"
    )
    private String addressLine;

    @NotBlank(message = "{validation.zip.blank}")
    @Pattern(
            regexp = "^[1-9][0-9]{5}$",
            message = "{validation.zip.invalid}"
    )
    private String zipCode;
}
