package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddCategoryRequest {
    @NotBlank(message = "{validation.category.name.required}")
    @Size(min = 2, max = 50, message = "{validation.category.name.size}")
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9 ]*$",
            message = "{validation.category.name.pattern}"
    )
    private String name;

    private UUID parentId;
}
