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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        try {
            log.info("[register] 🚀 entry: username={} roles={}", request.getUsername(), request.getRoles());
            log.debug("[register] 🔍 checking username uniqueness: username={}", request.getUsername());
            if (userRepository.existsByUsername(request.getUsername())) {
                log.warn("[register] ⚠️ username already taken: username={}", request.getUsername());
                throw new BadRequestException("Username already taken");
            }

            log.debug("[register] 🔍 checking email uniqueness: username={}", request.getUsername());
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("[register] ⚠️ email already registered: username={}", request.getUsername());
                throw new BadRequestException("Email already registered");
            }

            log.debug("[register] 🔍 encoding password and building user: username={}", request.getUsername());
            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .isAdmin(false)
                    .build();

            user = userRepository.save(user);
            log.info("[register] ✅ user saved: userId={}", user.getId());

            for (RoleType role : request.getRoles()) {
                log.debug("[register] 🔍 assigning role: userId={} role={}", user.getId(), role);
                UserRole userRole = UserRole.builder()
                        .user(user)
                        .role(role)
                        .build();
                userRoleRepository.save(userRole);
            }
            log.info("[register] ✅ roles assigned: userId={} roles={}", user.getId(), request.getRoles());
            userRoleRepository.flush();
            userRepository.flush();

            entityManager.clear();

            user = userRepository.findById(user.getId()).orElseThrow();
            log.info("[register] 🔍 roles after reload: {}", user.getRoles());

            String activeRole = user.getRoles().size() == 1
                    ? user.getRoles().get(0).getRole().name()
                    : null;
            log.debug("[register] 🔍 active role resolved: userId={} activeRole={} requiresRoleSelection={}",
                    user.getId(), activeRole, activeRole == null);

            String token = jwtTokenProvider.generateToken(user, activeRole);
            List<String> roles = user.getRoles().stream()
                    .map(r -> r.getRole().name()).toList();
            log.info("[register] ✅ token generated: userId={} activeRole={} requiresRoleSelection={}",
                    user.getId(), activeRole, activeRole == null);

            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .roles(roles)
                    .activeRole(activeRole)
                    .requiresRoleSelection(activeRole == null)
                    .build();
            log.info("[register] ✅ exit: userId={} username={} roles={} activeRole={} requiresRoleSelection={}",
                    user.getId(), user.getUsername(), roles, activeRole, response.isRequiresRoleSelection());
            return response;
        } catch (BadRequestException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("[register] ❌ unexpected error: message={}", e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            log.info("[login] 🚀 entry: username={}", request.getUsername());
            log.warn("[login] ⚠️ not implemented: username={}", request.getUsername());
            log.info("[login] ✅ exit: username={} result={}", request.getUsername(), null);
            return null;
        } catch (RuntimeException e) {
            log.error("[login] ❌ unexpected error: message={}", e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthResponse selectRole(SelectRoleRequest request, UUID userId) {
        try {
            log.info("[selectRole] 🚀 entry: userId={} role={}", userId, request.getRole());
            log.debug("[selectRole] 🔍 finding user: userId={}", userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("[selectRole] ⚠️ user not found: userId={}", userId);
                        return new ResourceNotFoundException("User not found");
                    });
            log.debug("[selectRole] ✅ user found: userId={} username={}", user.getId(), user.getUsername());

            log.debug("[selectRole] 🔍 checking role membership: userId={} role={}", userId, request.getRole());
            boolean hasRole = user.getRoles().stream()
                    .anyMatch(r -> r.getRole().name().equals(request.getRole()));

            if (!hasRole) {
                log.warn("[selectRole] ⚠️ role not assigned to user: userId={} role={}", userId, request.getRole());
                throw new BadRequestException("User does not have role: " + request.getRole());
            }

            String token = jwtTokenProvider.generateToken(user, request.getRole());
            List<String> roles = user.getRoles().stream()
                    .map(r -> r.getRole().name()).toList();
            log.info("[selectRole] ✅ token generated: userId={} activeRole={}", userId, request.getRole());

            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .roles(roles)
                    .activeRole(request.getRole())
                    .requiresRoleSelection(false)
                    .build();
            log.info("[selectRole] ✅ exit: userId={} username={} roles={} activeRole={} requiresRoleSelection={}",
                    user.getId(), user.getUsername(), roles, response.getActiveRole(), response.isRequiresRoleSelection());
            return response;
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("[selectRole] ❌ unexpected error: message={}", e.getMessage());
            throw e;
        }
    }

    @Override
    public UserProfileResponse getProfile(UUID userId) {
        try {
            log.info("[getProfile] 🚀 entry: userId={}", userId);
            log.warn("[getProfile] ⚠️ not implemented: userId={}", userId);
            log.info("[getProfile] ✅ exit: userId={} result={}", userId, null);
            return null;
        } catch (RuntimeException e) {
            log.error("[getProfile] ❌ unexpected error: message={}", e.getMessage());
            throw e;
        }
    }
}
