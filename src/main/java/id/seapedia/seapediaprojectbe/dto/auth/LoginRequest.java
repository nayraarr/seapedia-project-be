package id.seapedia.seapediaprojectbe.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username max 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
