package org.project.ttnecommerce.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryFilterResponse {
    private List<MetadataFieldWithValuesResponse> metadata;
    private List<String> brands;
    private PriceRangeResponse priceRange;
}