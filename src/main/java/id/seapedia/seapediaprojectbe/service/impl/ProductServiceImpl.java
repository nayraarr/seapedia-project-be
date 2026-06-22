package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.product.ProductResponse;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.Product;
import id.seapedia.seapediaprojectbe.repository.ProductRepository;
import id.seapedia.seapediaprojectbe.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("[getAllProducts] 🚀 entry");
        List<Product> products = productRepository.findAll();
        log.info("[getAllProducts] ✅ found {} products", products.size());
        return products.stream().map(this::toResponse).toList();
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        log.info("[getProductById] 🚀 entry: id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[getProductById] ⚠️ product not found: id={}", id);
                    return new ResourceNotFoundException("Product not found");
                });
        log.info("[getProductById] ✅ found: id={} name={}", product.getId(), product.getName());
        return toResponse(product);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}