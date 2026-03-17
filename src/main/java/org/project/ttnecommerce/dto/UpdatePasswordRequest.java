package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePasswordRequest {

    @NotBlank(message = "Current password is required")
    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]+$",
            message = "Password must contain uppercase, lowercase, digit and special character"
    )
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}