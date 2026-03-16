package org.project.ttnecommerce.dto;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private UUID addressId;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}