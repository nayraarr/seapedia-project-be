package id.seapedia.seapediaprojectbe.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressSnapshotResponse {
    private UUID addressId;
    private String recipientName;
    private String phone;
    private String fullAddress;
    private String city;
    private String postalCode;
}
