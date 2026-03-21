package org.project.ttnecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMetadataFieldRequest {

    @NotBlank(message = "{validation.field.name.required}")
    @Size(min = 2, max = 50, message = "{validation.field.name.size}")
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9 ]*$",
            message = "{validation.field.name.pattern}"
    )
    private String name;
}
