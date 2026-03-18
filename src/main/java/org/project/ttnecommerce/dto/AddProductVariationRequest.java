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

    @NotNull(message = "Product Id is mandatory")
    private UUID productId;

    @NotNull(message = "Quantity is mandatory")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantityAvailable;

    @NotNull(message = "Price is mandatory")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    @NotNull(message = "Metadata is mandatory")
    private String metadata;

    @NotNull(message = "Primary image is mandatory")
    private MultipartFile primaryImage;

    private List<MultipartFile> secondaryImages;
}