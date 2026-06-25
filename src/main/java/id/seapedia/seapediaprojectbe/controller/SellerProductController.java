package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.product.ProductRequest;
import id.seapedia.seapediaprojectbe.dto.product.ProductResponse;
import id.seapedia.seapediaprojectbe.security.CustomUserDetails;
import id.seapedia.seapediaprojectbe.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seller/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerProductController {

    private final ProductService productService;

    private UUID getSellerId(Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return user.getUserId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getMyProducts(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("OK", productService.getMyProducts(getSellerId(auth))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Produk berhasil dibuat",
                productService.createProduct(getSellerId(auth), request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id, @Valid @RequestBody ProductRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Produk berhasil diupdate",
                productService.updateProduct(getSellerId(auth), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable UUID id, Authentication auth) {
        productService.deleteProduct(getSellerId(auth), id);
        return ResponseEntity.ok(ApiResponse.success("Produk berhasil dihapus", null));
    }
}
