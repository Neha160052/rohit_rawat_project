package org.project.ttnecommerce.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddAddressResponse {
    private UUID addressId;
    private String city;
    private String state;
    private String country;
    private String addressLine;
    private String label;
    private String zipCode;
}