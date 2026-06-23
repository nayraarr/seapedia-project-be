package id.seapedia.seapediaprojectbe.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AdminUserResponse {
    private UUID id;
    private String username;
    private String email;
    private boolean isAdmin;
    private List<String> roles;
    private LocalDateTime createdAt;
}
