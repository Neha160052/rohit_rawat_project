package org.project.ttnecommerce.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfileResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private String contact;
    private String profileImage;

}
