package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "{validation.token.required}")
    private String token;

    @NotBlank(message = "{validation.password.required}")
    private String password;

    @NotBlank(message = "{validation.confirm.password.required}")
    private String confirmPassword;
}
