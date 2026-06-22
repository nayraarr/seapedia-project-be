package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.address.AddressRequest;
import id.seapedia.seapediaprojectbe.dto.address.AddressResponse;
import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.AddressService;
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
@RequestMapping("/api/buyer/addresses")
@RequiredArgsConstructor
public class BuyerAddressController {

    private final AddressService addressService;

    @GetMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[GET /buyer/addresses] 🚀 userId={}", userDetails.getUserId());
        List<AddressResponse> data = addressService.getMyAddresses(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Addresses fetched", data));
    }

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[POST /buyer/addresses] 🚀 userId={}", userDetails.getUserId());
        AddressResponse data = addressService.createAddress(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Address created", data));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[PUT /buyer/addresses/{}] 🚀 userId={}", id, userDetails.getUserId());
        AddressResponse data = addressService.updateAddress(userDetails.getUserId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated", data));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[DELETE /buyer/addresses/{}] 🚀 userId={}", id, userDetails.getUserId());
        addressService.deleteAddress(userDetails.getUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted", null));
    }

    @PatchMapping("/{id}/default")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefault(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[PATCH /buyer/addresses/{}/default] 🚀 userId={}", id, userDetails.getUserId());
        AddressResponse data = addressService.setDefaultAddress(userDetails.getUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Default address updated", data));
    }
}