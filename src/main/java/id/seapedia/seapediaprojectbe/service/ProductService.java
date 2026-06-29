package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.product.ProductRequest;
import id.seapedia.seapediaprojectbe.dto.product.ProductResponse;
import id.seapedia.seapediaprojectbe.model.ProductCategory;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(ProductCategory category);
    ProductResponse getProductById(UUID id);
    ProductResponse createProduct(UUID sellerId, ProductRequest request);
    ProductResponse updateProduct(UUID sellerId, UUID productId, ProductRequest request);
    void deleteProduct(UUID sellerId, UUID productId);
    List<ProductResponse> getMyProducts(UUID sellerId);
    List<ProductResponse> getProductsByStore(UUID storeId);
    List<ProductResponse> getSimilarProducts(UUID productId, int limit);
}