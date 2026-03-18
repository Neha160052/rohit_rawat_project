package org.project.ttnecommerce.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddCategoryMetadataRequest {
    private UUID categoryId;
    private List<MetadataFieldValuesRequest> metadata;

}