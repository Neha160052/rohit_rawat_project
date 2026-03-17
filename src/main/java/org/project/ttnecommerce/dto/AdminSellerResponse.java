package org.project.ttnecommerce.dto;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSellerResponse {
    private UUID id;
    private String fullName;
    private String email;
    private Boolean isActive;
    private String companyName;
    private String companyContact;
    private String companyAddress;
}