package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.project.ttnecommerce.Enum.ProductAction;

import java.util.UUID;
@Getter
@Setter
public class ProductStatusUpdateRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Action is required")
    private ProductAction action;
}