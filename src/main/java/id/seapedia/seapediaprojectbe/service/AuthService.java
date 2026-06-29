package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.auth.*;

import java.util.UUID;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse selectRole(SelectRoleRequest request, UUID userId);
    UserProfileResponse getProfile(UUID userId);
    UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request);
    FinancialSummaryResponse getFinancialSummary(UUID userId);
    void logout(String token);
    AuthResponse refresh(String token);
}