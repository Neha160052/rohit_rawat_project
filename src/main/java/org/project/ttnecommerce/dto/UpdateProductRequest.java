package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UpdateProductRequest {

    @NotNull(message = "Product ID is mandatory")
    private UUID productId;

    private String name;
    private String description;
    private Boolean isCancellable;
    private Boolean isReturnable;
}