package id.seapedia.seapediaprojectbe.repository;

import id.seapedia.seapediaprojectbe.model.DeliveryMethod;
import id.seapedia.seapediaprojectbe.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import id.seapedia.seapediaprojectbe.model.OrderStatus;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByBuyerIdOrderByCreatedAtDesc(UUID buyerId);
    List<Order> findByStoreIdOrderByCreatedAtDesc(UUID storeId);
    Optional<Order> findByIdAndBuyerId(UUID id, UUID buyerId);
    Optional<Order> findByIdAndStoreId(UUID id, UUID storeId);
    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'SELESAI'")
    Long sumTotalRevenue();

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.updatedAt < :threshold")
    List<Order> findByStatusAndUpdatedAtBefore(
            @Param("status") OrderStatus status,
            @Param("threshold") java.time.LocalDateTime threshold
    );

    @Query("SELECT o FROM Order o WHERE o.status NOT IN :finalStatuses AND o.createdAt < :threshold AND o.deliveryMethod = :deliveryMethod")
    List<Order> findOverdueByDeliveryMethod(
            @Param("finalStatuses") List<OrderStatus> finalStatuses,
            @Param("threshold") LocalDateTime threshold,
            @Param("deliveryMethod") DeliveryMethod deliveryMethod
    );

    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.status NOT IN :finalStatuses")
    Optional<Order> findActiveOrderById(
            @Param("id") UUID id,
            @Param("finalStatuses") List<OrderStatus> finalStatuses
    );
}
