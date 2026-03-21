package org.project.ttnecommerce.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class VariationPageResponse {

    private List<VariationDetailResponse> content;

    private int offset;
    private int max;

    private long totalElements;
}