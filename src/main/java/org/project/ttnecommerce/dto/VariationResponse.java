package org.project.ttnecommerce.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Builder
public class VariationResponse {
    private UUID id;
    private Double price;
    private Integer quantity;
    private String primaryImage;
}
