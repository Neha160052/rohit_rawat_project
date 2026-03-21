package org.project.ttnecommerce.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterSellerRequest {

    @NotBlank(message = "{validation.email.required.mandatory}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.password.required.mandatory}")
    @Size(min = 8, max = 20, message = "{validation.password.size.8.20}")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
            message = "{validation.password.pattern.general}"
    )
    private String password;

    @NotBlank(message = "{validation.confirm.password.required.mandatory}")
    private String confirmPassword;

    @NotBlank(message = "{validation.gst.required}")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "{validation.gst.invalid}"
    )
    private String gst;

    @NotBlank(message = "{validation.company.name.required}")
    @Size(min = 2, max = 100, message = "{validation.company.name.size}")
    private String companyName;

    @NotBlank(message = "{validation.company.contact.required}")
    @Pattern(regexp = "^[0-9]{10}$", message = "{validation.company.contact.invalid}")
    private String companyContact;

    @NotBlank(message = "{validation.first.name.required}")
    @Pattern(regexp = "^[A-Za-z]{2,50}$", message = "{validation.first.name.alpha}")
    private String firstName;

    @NotBlank(message = "{validation.last.name.required}")
    @Pattern(regexp = "^[A-Za-z]{2,50}$", message = "{validation.last.name.alpha}")
    private String lastName;

    @Valid
    @NotNull(message = "{validation.address.required}")
    private AddressRequest address;
}
