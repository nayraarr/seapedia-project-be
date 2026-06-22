package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.store.StoreRequest;
import id.seapedia.seapediaprojectbe.dto.store.StoreResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // SELLER endpoints
    @PostMapping("/seller/store")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(
            @Valid @RequestBody StoreRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[POST /seller/store] 🚀 userId={}", userDetails.getUserId());
        StoreResponse data = storeService.createStore(request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Store created", data));
    }

    @PutMapping("/seller/store")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @Valid @RequestBody StoreRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[PUT /seller/store] 🚀 userId={}", userDetails.getUserId());
        StoreResponse data = storeService.updateStore(request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Store updated", data));
    }

    @GetMapping("/seller/store")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<StoreResponse>> getMyStore(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[GET /seller/store] 🚀 userId={}", userDetails.getUserId());
        StoreResponse data = storeService.getMyStore(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Store fetched", data));
    }

    // PUBLIC endpoints
    @GetMapping("/stores")
    public ResponseEntity<ApiResponse<List<StoreResponse>>> getAllStores() {
        log.info("[GET /api/stores] 🚀 request received");
        List<StoreResponse> data = storeService.getAllStores();
        log.info("[GET /api/stores] ✅ returning {} stores", data.size());
        return ResponseEntity.ok(ApiResponse.success("Stores fetched", data));
    }

    @GetMapping("/stores/{id}")
    public ResponseEntity<ApiResponse<StoreResponse>> getStoreById(@PathVariable UUID id) {
        log.info("[GET /stores/{}] 🚀", id);
        StoreResponse data = storeService.getStoreById(id);
        return ResponseEntity.ok(ApiResponse.success("Store fetched", data));
    }
}
