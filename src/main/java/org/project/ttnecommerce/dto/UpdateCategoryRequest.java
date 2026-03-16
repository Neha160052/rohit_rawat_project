package org.project.ttnecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateCategoryRequest {

    private UUID id;

    private String name;

}