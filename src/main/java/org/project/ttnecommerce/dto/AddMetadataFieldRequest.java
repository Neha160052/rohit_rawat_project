package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMetadataFieldRequest {

    @NotBlank(message = "Field name is mandatory")
    private String name;

}