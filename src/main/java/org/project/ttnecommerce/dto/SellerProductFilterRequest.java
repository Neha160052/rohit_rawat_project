package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class SellerProductFilterRequest {

    @Min(value = 1, message = "{validation.max.min}")
    @Max(value = 100, message = "{validation.max.max}")
    private Integer max = 10;

    @Min(value = 0, message = "{validation.offset.min}")
    private Integer offset = 0;

    @Pattern(
            regexp = "id|name|brand|createdDate|isActive",
            message = "{validation.sort.seller}"
    )
    private String sort = "id";

    @Pattern(
            regexp = "(?i)asc|desc",
            message = "{validation.order}"
    )
    private String order = "asc";

    private UUID productId;
    private UUID categoryId;
}
