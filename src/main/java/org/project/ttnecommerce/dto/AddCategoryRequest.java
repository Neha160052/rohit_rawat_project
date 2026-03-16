package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class AddCategoryRequest {
    @NotBlank(message = "Category name is mandatory")
    private String name;

    private UUID parentId;
}