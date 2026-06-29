package id.seapedia.seapediaprojectbe.controller;

import id.seapedia.seapediaprojectbe.dto.common.ApiResponse;
import id.seapedia.seapediaprojectbe.dto.product.ProductResponse;
import id.seapedia.seapediaprojectbe.model.ProductCategory;
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
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(
            @RequestParam(required = false) ProductCategory category) {
        log.info("[GET /api/products]  request received, category={}", category);
        List<ProductResponse> data;
        if (category != null) {
            data = productService.getProductsByCategory(category);
        } else {
            data = productService.getAllProducts();
        }
        log.info("[GET /api/products]  returning {} products", data.size());
        return ResponseEntity.ok(ApiResponse.success("Products fetched", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        log.info("[GET /api/products/{}]  request received", id);
        ProductResponse data = productService.getProductById(id);
        log.info("[GET /api/products/{}]  returning product name={}", id, data.getName());
        return ResponseEntity.ok(ApiResponse.success("Product fetched", data));
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getSimilarProducts(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "8") int limit) {
        log.info("[GET /api/products/{}/similar]  request received, limit={}", id, limit);
        List<ProductResponse> data = productService.getSimilarProducts(id, limit);
        return ResponseEntity.ok(ApiResponse.success("Similar products fetched", data));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByStore(@PathVariable UUID storeId) {
        return ResponseEntity.ok(ApiResponse.success("Products fetched", productService.getProductsByStore(storeId)));
    }
}