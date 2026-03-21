package org.project.ttnecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class AddProductRequest {

    @NotBlank(message = "{validation.product.name.required}")
    @Size(min = 2, max = 100, message = "{validation.product.name.size}")
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9 ]*$",
            message = "{validation.product.name.pattern}"
    )
    private String name;

    @NotBlank(message = "{validation.brand.required}")
    @Size(min = 2, max = 50, message = "{validation.brand.size}")
    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9 ]*$",
            message = "{validation.brand.pattern}"
    )
    private String brand;

    @NotNull(message = "{validation.category.id.required}")
    private UUID categoryId;

    @Size(max = 500, message = "{validation.description.max}")
    private String description;

    private Boolean isCancellable = false;

    private Boolean isReturnable = false;
}