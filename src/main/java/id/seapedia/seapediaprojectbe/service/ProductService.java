package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.product.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(UUID id);
}