package org.project.ttnecommerce.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private String city;
    private String state;
    private String country;
    private String addressLine;
    private String zipCode;
}
