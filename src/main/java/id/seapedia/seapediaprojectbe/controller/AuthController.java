package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.auth.*;
import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse data = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse data = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", data));
    }

    @PostMapping("/select-role")
    public ResponseEntity<ApiResponse<AuthResponse>> selectRole(
            @Valid @RequestBody SelectRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AuthResponse data = authService.selectRole(request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Role selected", data));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> me(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse data = authService.getProfile(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", data));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse data = authService.updateProfile(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", data));
    }

    @GetMapping("/me/summary")
    public ResponseEntity<ApiResponse<FinancialSummaryResponse>> getFinancialSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        FinancialSummaryResponse data = authService.getFinancialSummary(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Financial summary fetched", data));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            authService.logout(header.substring(7));
        }
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Token tidak ditemukan", List.of("Authorization header missing")));
        }
        AuthResponse data = authService.refresh(header.substring(7));
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", data));
    }
}