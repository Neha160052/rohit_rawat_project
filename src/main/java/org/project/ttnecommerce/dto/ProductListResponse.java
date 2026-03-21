package org.project.ttnecommerce.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
@Builder
public class ProductListResponse {

    private List<AdminProductResponse> content;

    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
}