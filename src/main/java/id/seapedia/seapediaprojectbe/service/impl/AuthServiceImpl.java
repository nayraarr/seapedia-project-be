package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.auth.*;
import id.seapedia.seapediaprojectbe.exception.BadRequestException;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.RoleType;
import id.seapedia.seapediaprojectbe.model.User;
import id.seapedia.seapediaprojectbe.model.UserRole;
import id.seapedia.seapediaprojectbe.repository.UserRepository;
import id.seapedia.seapediaprojectbe.repository.UserRoleRepository;
import id.seapedia.seapediaprojectbe.security.JwtTokenProvider;
import id.seapedia.seapediaprojectbe.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isAdmin(false)
                .build();

        user = userRepository.save(user);

        for (RoleType role : request.getRoles()) {
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();
            userRoleRepository.save(userRole);
        }

        user = userRepository.findById(user.getId()).orElseThrow();

        String activeRole = user.getRoles().size() == 1
                ? user.getRoles().get(0).getRole().name()
                : null;

        String token = jwtTokenProvider.generateToken(user, activeRole);
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getRole().name()).toList();

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .roles(roles)
                .activeRole(activeRole)
                .requiresRoleSelection(activeRole == null)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public AuthResponse selectRole(SelectRoleRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getRole().name().equals(request.getRole()));

        if (!hasRole) {
            throw new BadRequestException("User does not have role: " + request.getRole());
        }

        String token = jwtTokenProvider.generateToken(user, request.getRole());
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getRole().name()).toList();

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .roles(roles)
                .activeRole(request.getRole())
                .requiresRoleSelection(false)
                .build();
    }

    @Override
    public UserProfileResponse getProfile(UUID userId) {
        return null;
    }
}
