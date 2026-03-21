package org.project.ttnecommerce.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProductResponse {

    private UUID productId;
    private String name;
    private String brand;
    private String description;

    private CategoryDto category;

    private List<String> primaryImages;

    private List<VariationResponse> variations;
}