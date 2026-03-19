package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.project.ttnecommerce.Enum.UserStatus;

@Getter
@Setter
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private UserStatus status;
}