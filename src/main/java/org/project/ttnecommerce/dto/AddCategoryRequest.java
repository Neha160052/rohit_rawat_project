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
    @NotBlank(message = "Category name is mandatory")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9 ]*$",
            message = "Category name must start with a letter and contain only alphabets, numbers and spaces"
    )
    private String name;

    private UUID parentId;
}