package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ProductFilterRequest {

    @Min(value = 1, message = "{validation.max.min}")
    @Max(value = 100, message = "{validation.max.max}")
    private Integer max = 10;

    @Min(value = 0, message = "{validation.offset.min}")
    private Integer offset = 0;

    @Pattern(
            regexp = "id|name|brand",
            message = "{validation.sort.customer}"
    )
    private String sort = "id";

    @Pattern(
            regexp = "(?i)asc|desc",
            message = "{validation.order}"
    )
    private String order = "asc";

    private UUID sellerId;

    private UUID categoryId;

    private UUID productId;

}
