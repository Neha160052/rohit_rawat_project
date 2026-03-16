package org.project.ttnecommerce.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private UUID id;
    private String name;
    private List<ParentCategoryDto> parentCategories;
    private List<ChildCategoryDto> children;
    private List<CategoryMetadataFieldResponse> metadataFields;

}
