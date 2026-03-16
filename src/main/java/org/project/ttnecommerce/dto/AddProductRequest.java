package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class AddProductRequest {

    @NotBlank(message = "Product name is mandatory")
    private String name;

    @NotBlank(message = "Brand is mandatory")
    private String brand;

    @NotNull(message = "Category Id is mandatory")
    private UUID categoryId;

    private String description;

    private Boolean isCancellable = false;

    private Boolean isReturnable = false;
}