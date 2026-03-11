package org.project.ttnecommerce.dto;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerProfileResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private String companyName;
    private String companyContact;
    private String gst;
    private String image;

    private AddressResponse address;
}
