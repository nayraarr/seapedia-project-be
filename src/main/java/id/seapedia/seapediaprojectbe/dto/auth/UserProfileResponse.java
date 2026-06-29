package id.seapedia.seapediaprojectbe.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String username;
    private String fullName;
    private String email;
    private Boolean isAdmin;
    private List<String> roles;
    private String activeRole;
    private LocalDateTime createdAt;
}