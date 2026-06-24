package id.seapedia.seapediaprojectbe.service.impl;

import id.seapedia.seapediaprojectbe.dto.product.ProductRequest;
import id.seapedia.seapediaprojectbe.dto.product.ProductResponse;
import id.seapedia.seapediaprojectbe.exception.BadRequestException;
import id.seapedia.seapediaprojectbe.exception.ResourceNotFoundException;
import id.seapedia.seapediaprojectbe.model.Product;
import id.seapedia.seapediaprojectbe.model.Store;
import id.seapedia.seapediaprojectbe.repository.ProductRepository;
import id.seapedia.seapediaprojectbe.repository.StoreRepository;
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
    private final StoreRepository storeRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("[getAllProducts]  entry");
        List<Product> products = productRepository.findAll();
        log.info("[getAllProducts]  found {} products", products.size());
        return products.stream().map(this::toResponse).toList();
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        log.info("[getProductById]  entry: id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[getProductById]  product not found: id={}", id);
                    return new ResourceNotFoundException("Product not found");
                });
        log.info("[getProductById]  found: id={} name={}", product.getId(), product.getName());
        return toResponse(product);
    }

    @Override
    public List<ProductResponse> getMyProducts(UUID sellerId) {
        Store store = storeRepository.findByOwnerId(sellerId)
                .orElseThrow(() -> new BadRequestException("Kamu belum punya toko"));
        return productRepository.findByStoreId(store.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ProductResponse createProduct(UUID sellerId, ProductRequest request) {
        Store store = storeRepository.findByOwnerId(sellerId)
                .orElseThrow(() -> new BadRequestException("Kamu belum punya toko"));
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .store(store)
                .build();
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID sellerId, UUID productId, ProductRequest request) {
        Store store = storeRepository.findByOwnerId(sellerId)
                .orElseThrow(() -> new BadRequestException("Kamu belum punya toko"));
        Product product = productRepository.findByIdAndStoreId(productId, store.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan atau bukan milikmu"));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(UUID sellerId, UUID productId) {
        Store store = storeRepository.findByOwnerId(sellerId)
                .orElseThrow(() -> new BadRequestException("Kamu belum punya toko"));
        Product product = productRepository.findByIdAndStoreId(productId, store.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan atau bukan milikmu"));
        productRepository.delete(product);
    }

    @Override
    public List<ProductResponse> getProductsByStore(UUID storeId) {
        return productRepository.findByStoreId(storeId)
                .stream().map(this::toResponse).toList();
    }

    private ProductResponse toResponse(Product p) {
        ProductResponse res = new ProductResponse();
        res.setId(p.getId());
        res.setName(p.getName());
        res.setDescription(p.getDescription());
        res.setPrice(p.getPrice());
        res.setStock(p.getStock());
        res.setStoreId(p.getStore().getId());
        res.setStoreName(p.getStore().getName());
        res.setCreatedAt(p.getCreatedAt());
        res.setUpdatedAt(p.getUpdatedAt());
        return res;
    }
}