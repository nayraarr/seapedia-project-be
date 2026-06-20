package id.seapedia.seapediaprojectbe.dto.auth;

import id.seapedia.seapediaprojectbe.model.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format invalid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password minimum 6 characters")
    private String password;

    @NotEmpty(message = "At least one role is required")
    private List<RoleType> roles;
}
