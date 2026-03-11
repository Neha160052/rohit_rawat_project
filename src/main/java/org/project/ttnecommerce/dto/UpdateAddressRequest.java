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

    @Size(max = 255, message = "Address line too long")
    private String addressLine;
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "City must contain only letters")
    private String city;
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "State must contain only letters")
    private String state;
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Country must contain only letters")
    private String country;
    @Pattern(regexp = "^[0-9]{6}$", message = "Zip code must be 6 digits")
    private String zipCode;
}