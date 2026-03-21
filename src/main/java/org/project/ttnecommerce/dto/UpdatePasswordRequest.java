package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePasswordRequest {

    @NotBlank(message = "{validation.current.password.required}")
    @Size(min = 8, max = 15, message = "{validation.password.size.8.15}")
    private String currentPassword;

    @NotBlank(message = "{validation.new.password.required}")
    @Size(min = 8, max = 15, message = "{validation.password.size.8.15}")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]+$",
            message = "{validation.password.pattern.general}"
    )
    private String newPassword;

    @NotBlank(message = "{validation.confirm.password.required.long}")
    private String confirmPassword;
}
