package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.cart.AddToCartRequest;
import id.seapedia.seapediaprojectbe.dto.cart.CartSummaryResponse;
import id.seapedia.seapediaprojectbe.dto.cart.UpdateCartItemRequest;
import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/buyer/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartSummaryResponse>> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[GET /api/buyer/cart] buyer={}", userDetails.getUserId());
        CartSummaryResponse data = cartService.getCart(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Cart fetched", data));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> addItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request) {
        log.info("[POST /api/buyer/cart/items] buyer={} product={}", userDetails.getUserId(), request.getProductId());
        CartSummaryResponse data = cartService.addItem(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Produk ditambahkan ke keranjang", data));
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> updateItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        log.info("[PATCH /api/buyer/cart/items/{}] buyer={}", cartItemId, userDetails.getUserId());
        CartSummaryResponse data = cartService.updateItem(userDetails.getUserId(), cartItemId, request);
        return ResponseEntity.ok(ApiResponse.success("Quantity diperbarui", data));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> removeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID cartItemId) {
        log.info("[DELETE /api/buyer/cart/items/{}] buyer={}", cartItemId, userDetails.getUserId());
        CartSummaryResponse data = cartService.removeItem(userDetails.getUserId(), cartItemId);
        return ResponseEntity.ok(ApiResponse.success("Item dihapus dari keranjang", data));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[DELETE /api/buyer/cart] buyer={}", userDetails.getUserId());
        cartService.clearCart(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Keranjang dikosongkan", null));
    }
}