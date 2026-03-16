package org.project.ttnecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SellerCategoryResponse {

    private UUID id;
    private String name;
    private List<String> parentCategoryChain;
    private List<MetadataFieldWithValuesResponse> metadata;

}