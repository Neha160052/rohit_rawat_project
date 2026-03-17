package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

    @NotBlank(message = "City cannot be blank")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "City must contain only alphabets")
    private String city;

    @NotBlank(message = "State cannot be blank")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "State must contain only alphabets")
    private String state;

    @NotBlank(message = "Country cannot be blank")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Country must contain only alphabets")
    private String country;

    @NotBlank(message = "Address line cannot be blank")
    private String addressLine;

    @NotBlank(message = "Zip code cannot be blank")
    @Pattern(regexp = "^[0-9]{6}$", message = "Zip code must be a valid 6 digit number")
    private String zipCode;
}