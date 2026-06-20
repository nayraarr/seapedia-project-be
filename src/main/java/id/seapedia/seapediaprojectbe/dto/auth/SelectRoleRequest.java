package id.seapedia.seapediaprojectbe.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectRoleRequest {
    @NotBlank(message = "Role is required")
    private String role;
}
