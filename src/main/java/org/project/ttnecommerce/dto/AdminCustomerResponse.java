package org.project.ttnecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminCustomerResponse {

    private UUID id;
    private String fullName;
    private String email;
    private Boolean isActive;
}