package id.seapedia.seapediaprojectbe.security;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private UUID userId;
    private String username;
    private String fullName;
    private String password;
    private String activeRole;
    private Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails fromClaims(Claims claims) {
        String activeRole = (String) claims.get("activeRole");
        Boolean isAdmin = (Boolean) claims.get("isAdmin");

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (Boolean.TRUE.equals(isAdmin)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        if (activeRole != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + activeRole));
        }

        return new CustomUserDetails(
                UUID.fromString(claims.getSubject()),
                (String) claims.get("username"),
                (String) claims.get("fullName"),
                null,
                activeRole,
                authorities
        );
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
