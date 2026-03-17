package org.project.ttnecommerce.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterSellerRequest {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain uppercase, lowercase, digit and special character"
    )
    private String password;

    @NotBlank(message = "Confirm password is mandatory")
    private String confirmPassword;

    @NotBlank(message = "GST is mandatory")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Invalid GST format"
    )
    private String gst;

    @NotBlank(message = "Company name is mandatory")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String companyName;

    @NotBlank(message = "Company contact is mandatory")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String companyContact;

    @NotBlank(message = "First name is mandatory")
    @Pattern(regexp = "^[A-Za-z]{2,50}$", message = "First name must contain only alphabets")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Pattern(regexp = "^[A-Za-z]{2,50}$", message = "Last name must contain only alphabets")
    private String lastName;

    @Valid
    @NotNull(message = "Address is mandatory")
    private AddressRequest address;
}