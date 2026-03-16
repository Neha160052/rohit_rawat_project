package org.project.ttnecommerce.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PriceRangeResponse {
    private Double min;
    private Double max;
}