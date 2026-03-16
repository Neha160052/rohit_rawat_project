package org.project.ttnecommerce.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MetadataFieldWithValuesResponse {

    private UUID fieldId;
    private String fieldName;
    private List<String> values;

}