package org.project.ttnecommerce.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CategoryDto {
    private UUID id;
    private String name;
}