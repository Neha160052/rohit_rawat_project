package org.project.ttnecommerce.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerProfileUpdateRequest {

    @NotBlank(message = "{validation.first.name.blank}")
    @Pattern(regexp = "^[A-Za-z]+$", message = "{validation.first.name.alpha}")
    private String firstName;

    @NotBlank(message = "{validation.last.name.blank}")
    @Pattern(regexp = "^[A-Za-z]+$", message = "{validation.last.name.alpha}")
    private String lastName;

    @NotBlank(message = "{validation.company.name.blank}")
    private String companyName;

    @NotBlank(message = "{validation.company.contact.blank}")
    @Pattern(regexp = "^[0-9]{10}$", message = "{validation.company.contact.valid}")
    private String companyContact;

    @NotBlank(message = "{validation.gst.blank}")
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[A-Z0-9]{1}Z[A-Z0-9]{1}$", message = "{validation.gst.invalid}")
    private String gst;

    @Valid
    @NotNull(message = "{validation.address.notnull}")
    private AddressRequest address;
}
