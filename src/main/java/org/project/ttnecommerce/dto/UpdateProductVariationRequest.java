package org.project.ttnecommerce.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateProductVariationRequest {

    @NotNull(message = "Variation ID cannot be null")
    private UUID variationId;

    private Double price;
    private Integer quantity;
    private Boolean isActive;

    private MultipartFile primaryImage;
    private List<MultipartFile> secondaryImages;

    private String metadata;
}