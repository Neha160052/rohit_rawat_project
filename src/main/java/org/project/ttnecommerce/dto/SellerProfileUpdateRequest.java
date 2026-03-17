package org.project.ttnecommerce.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerProfileUpdateRequest {

    @NotBlank(message = "First name cannot be blank")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only alphabets")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only alphabets")
    private String lastName;

    @NotBlank(message = "Company name cannot be blank")
    private String companyName;

    @NotBlank(message = "Company contact cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Company contact must be a valid 10 digit number")
    private String companyContact;

    @NotBlank(message = "GST number cannot be blank")
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[A-Z0-9]{1}Z[A-Z0-9]{1}$", message = "Invalid GST format")
    private String gst;

    @Valid
    @NotNull(message = "Address cannot be null")
    private AddressRequest address;
}