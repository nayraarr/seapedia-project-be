package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.product.ProductResponse;
import id.seapedia.seapediaprojectbe.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        log.info("[GET /api/products] 🚀 request received");
        List<ProductResponse> data = productService.getAllProducts();
        log.info("[GET /api/products] ✅ returning {} products", data.size());
        return ResponseEntity.ok(ApiResponse.success("Products fetched", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        log.info("[GET /api/products/{}] 🚀 request received", id);
        ProductResponse data = productService.getProductById(id);
        log.info("[GET /api/products/{}] ✅ returning product name={}", id, data.getName());
        return ResponseEntity.ok(ApiResponse.success("Product fetched", data));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByStore(@PathVariable UUID storeId) {
        return ResponseEntity.ok(ApiResponse.success("Products fetched", productService.getProductsByStore(storeId)));
    }
}