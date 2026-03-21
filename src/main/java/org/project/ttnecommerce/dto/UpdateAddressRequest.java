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
            message = "{validation.address.invalid}"
    )
    private String addressLine;

    @Pattern(
            regexp = "^(?!\\s*$)[A-Za-z]+(?: [A-Za-z]+)*$",
            message = "{validation.city.alpha.single_space.short}"
    )
    private String city;

    @Pattern(
            regexp = "^(?!\\s*$)[A-Za-z]+(?: [A-Za-z]+)*$",
            message = "{validation.state.alpha.single_space.short}"
    )
    private String state;

    @Pattern(
            regexp = "^(?!\\s*$)[A-Za-z]+(?: [A-Za-z]+)*$",
            message = "{validation.country.alpha.single_space.short}"
    )
    private String country;

    @Pattern(
            regexp = "^(?!\\s*$)[1-9][0-9]{5}$",
            message = "{validation.zip.invalid}"
    )
    private String zipCode;

    @Pattern(
            regexp = "^(?!\\s*$)[A-Za-z ]{2,50}$",
            message = "{validation.label.alpha.range}"
    )
    private String label;
}
