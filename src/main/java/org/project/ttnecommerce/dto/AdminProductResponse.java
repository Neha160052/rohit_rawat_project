package org.project.ttnecommerce.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AdminProductResponse {

    private UUID productId;
    private String name;
    private String brand;
    private String description;

    private Boolean isActive;

    private CategoryDto category;

    private List<AdminVariationResponse> variations;
}