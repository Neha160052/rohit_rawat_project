package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterCustomerRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$")
    private String phone;

    @NotBlank
    @Size(min = 8, max = 15)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "Password must be 8-20 chars with uppercase, lowercase, digit and special character"
    )
    private String password;

    @NotBlank
    private String confirmPassword;

    @Pattern(regexp = "^[A-Za-z]+$", message = "First name should contain only alphabets")
    private String firstName;

    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name should contain only alphabets")
    private String lastName;
}