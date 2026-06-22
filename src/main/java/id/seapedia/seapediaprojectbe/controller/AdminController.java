package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<String>> getDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[GET /api/admin/dashboard] 🚀 userId={}", userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard", "Welcome, Admin!"));
    }
}