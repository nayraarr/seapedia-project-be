package id.seapedia.seapediaprojectbe.repository;

import id.seapedia.seapediaprojectbe.model.Product;
import id.seapedia.seapediaprojectbe.model.ProductCategory;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByStockGreaterThan(int stock);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Product> findByStoreId(UUID storeId);
    Optional<Product> findByIdAndStoreId(UUID id, UUID storeId);
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByCategoryAndIdNot(ProductCategory category, UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") UUID id);
}
