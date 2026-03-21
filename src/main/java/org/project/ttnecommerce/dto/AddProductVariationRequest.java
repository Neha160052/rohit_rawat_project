package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class AddProductVariationRequest {

    @NotNull(message = "{validation.product.id.required}")
    private UUID productId;

    @NotNull(message = "{validation.quantity.required}")
    @Min(value = 0, message = "{validation.quantity.min}")
    private Integer quantityAvailable;

    @NotNull(message = "{validation.price.required}")
    @Min(value = 0, message = "{validation.price.min}")
    private Double price;

    @NotNull(message = "{validation.metadata.required}")
    private String metadata;

    @NotNull(message = "{validation.primary.image.required}")
    private MultipartFile primaryImage;

    private List<MultipartFile> secondaryImages;
}
