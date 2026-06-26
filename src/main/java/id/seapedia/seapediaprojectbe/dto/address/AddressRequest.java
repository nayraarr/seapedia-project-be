package id.seapedia.seapediaprojectbe.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    @NotBlank(message = "Label is required")
    private String label;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Phone number format invalid")
    private String phone;

    @NotBlank(message = "Full address is required")
    private String fullAddress;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotNull(message = "isDefault is required")
    private Boolean isDefault;
}