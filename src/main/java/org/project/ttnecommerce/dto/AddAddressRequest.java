package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddAddressRequest {

    @NotBlank(message = "Address cannot be empty")
    @Size(min = 2, max = 255, message = "Address must be between 5 and 255 characters")
    private String addressLine;

    @NotBlank(message = "City cannot be empty")
    @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "City must contain only alphabets")
    private String city;

    @NotBlank(message = "State cannot be empty")
    @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "State must contain only alphabets")
    private String state;

    @NotBlank(message = "Country cannot be empty")
    @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "Country must contain only alphabets")
    private String country;

    @NotBlank(message = "Zip code cannot be empty")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Zip code must be 6 digits and cannot start with 0")
    private String zipCode;

    @NotBlank(message = "Label cannot be empty")
    @Pattern(regexp = "^(HOME|WORK|OTHER)$", message = "Label must be HOME, WORK or OTHER")
    private String label;
}