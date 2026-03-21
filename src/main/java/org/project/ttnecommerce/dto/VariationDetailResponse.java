package org.project.ttnecommerce.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class VariationDetailResponse {

    private UUID variationId;
    private Double price;
    private Integer quantityAvailable;

    private String metadata;

    private String primaryImage;
    private List<String> secondaryImages;

    private Boolean isActive;
    private Boolean isDeleted;
}