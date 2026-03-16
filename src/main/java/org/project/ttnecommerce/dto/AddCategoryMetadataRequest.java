package org.project.ttnecommerce.dto;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AddCategoryMetadataRequest {
    private UUID categoryId;
    private List<MetadataFieldValuesRequest> metadata;
}