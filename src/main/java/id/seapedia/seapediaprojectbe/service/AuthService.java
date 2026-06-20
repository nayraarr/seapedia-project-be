package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.auth.AuthResponse;
import id.seapedia.seapediaprojectbe.dto.auth.LoginRequest;
import id.seapedia.seapediaprojectbe.dto.auth.RegisterRequest;
import id.seapedia.seapediaprojectbe.dto.auth.SelectRoleRequest;

import java.util.UUID;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse selectRole(SelectRoleRequest request, UUID userId);
    UserProfileResponse getProfile(UUID userId);
}