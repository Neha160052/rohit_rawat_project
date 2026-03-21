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

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.phone.required}")
    @Pattern(regexp = "^[0-9]{10}$", message = "{validation.phone.invalid}")
    private String phone;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 8, max = 15, message = "{validation.password.size.8.15}")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "{validation.password.pattern.customer}"
    )
    private String password;

    @NotBlank(message = "{validation.confirm.password.required}")
    private String confirmPassword;

    @Pattern(regexp = "^[A-Za-z]+$", message = "{validation.first.name.alpha.only}")
    private String firstName;

    @Pattern(regexp = "^[A-Za-z]+$", message = "{validation.last.name.alpha.only}")
    private String lastName;
}
