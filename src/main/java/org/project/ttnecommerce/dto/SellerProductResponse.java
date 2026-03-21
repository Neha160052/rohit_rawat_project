package org.project.ttnecommerce.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class SellerProductResponse {

    private UUID id;
    private String name;
    private String brand;
    private Boolean isActive;

    private CategoryResponse category;
}