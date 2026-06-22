package id.seapedia.seapediaprojectbe.repository;

import id.seapedia.seapediaprojectbe.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrderIdOrderByCreatedAtAsc(UUID orderId);
}
