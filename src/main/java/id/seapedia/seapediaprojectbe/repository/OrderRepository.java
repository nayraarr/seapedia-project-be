package id.seapedia.seapediaprojectbe.repository;

import id.seapedia.seapediaprojectbe.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByBuyerIdOrderByCreatedAtDesc(UUID buyerId);
    List<Order> findByStoreIdOrderByCreatedAtDesc(UUID storeId);
    Optional<Order> findByIdAndBuyerId(UUID id, UUID buyerId);
    Optional<Order> findByIdAndStoreId(UUID id, UUID storeId);
}
