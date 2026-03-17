package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterSellerRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    private String gst;

    @NotBlank
    private String companyName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String companyContact;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private AddressRequest address;

}