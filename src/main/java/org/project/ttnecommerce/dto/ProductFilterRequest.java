package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ProductFilterRequest {

    @Min(value = 1, message = "Max must be at least 1")
    @Max(value = 100, message = "Max cannot exceed 100")
    private Integer max = 10;

    @Min(value = 0, message = "Offset cannot be negative")
    private Integer offset = 0;

    @Pattern(
            regexp = "id|name|brand",
            message = "Sort must be one of: id, name, brand"
    )
    private String sort = "id";

    @Pattern(
            regexp = "(?i)asc|desc",
            message = "Order must be either asc or desc"
    )
    private String order = "asc";

    private UUID sellerId;

    private UUID categoryId;

    private UUID productId;
}