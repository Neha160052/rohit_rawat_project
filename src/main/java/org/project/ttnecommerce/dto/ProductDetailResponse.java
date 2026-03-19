package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProductDetailResponse {

    @NotNull(message = "Product ID is mandatory")
    private UUID productId;
    private String name;
    private String description;
    private String brand;

    private UUID categoryId;
    private String categoryName;

    private List<VariationResponse> variations;
}
