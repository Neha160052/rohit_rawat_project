package org.project.ttnecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Please provide the email")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Required Password")
    private String password;
}
