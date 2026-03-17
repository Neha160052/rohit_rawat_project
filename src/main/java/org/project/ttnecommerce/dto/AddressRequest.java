package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

    @NotBlank(message = "City cannot be blank")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]+(?:[ ][A-Za-z]+)*$", message = "City must contain only alphabets and single spaces between words")
    private String city;

    @NotBlank(message = "State cannot be blank")
    @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]+(?:[ ][A-Za-z]+)*$", message = "State must contain only alphabets and single spaces between words")
    private String state;

    @NotBlank(message = "Country cannot be blank")
    @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]+(?:[ ][A-Za-z]+)*$", message = "Country must contain only alphabets and single spaces between words")
    private String country;

    @NotBlank(message = "Address line cannot be blank")
    @Size(min = 2, max = 255, message = "Address must be between 5 and 255 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9 ,./#-]+$",
            message = "Address contains invalid characters"
    )
    private String addressLine;

    @NotBlank(message = "Zip code cannot be blank")
    @Pattern(
            regexp = "^[1-9][0-9]{5}$",
            message = "Zip code must be 6 digits and cannot start with 0"
    )
    private String zipCode;
}