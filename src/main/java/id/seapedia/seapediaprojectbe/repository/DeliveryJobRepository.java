package id.seapedia.seapediaprojectbe.repository;

import id.seapedia.seapediaprojectbe.model.DeliveryJob;
import id.seapedia.seapediaprojectbe.model.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryJobRepository extends JpaRepository<DeliveryJob, UUID> {
    Optional<DeliveryJob> findByOrderId(UUID orderId);

    List<DeliveryJob> findByOrder_StatusOrderByCreatedAtAsc(OrderStatus status);

    Optional<DeliveryJob> findByIdAndOrder_Status(UUID id, OrderStatus status);

    List<DeliveryJob> findByDriverIdAndOrder_StatusInOrderByTakenAtDesc(UUID driverId, List<OrderStatus> statuses);

    List<DeliveryJob> findByDriverIdAndOrder_StatusOrderByCompletedAtDesc(UUID driverId, OrderStatus status);

    long countByDriverIdAndOrder_Status(UUID driverId, OrderStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM DeliveryJob j WHERE j.id = :id AND j.order.status = :status")
    Optional<DeliveryJob> findByIdAndOrder_StatusForUpdate(@Param("id") UUID id, @Param("status") OrderStatus status);

    Optional<DeliveryJob> findByIdAndDriverIdAndOrder_Status(UUID id, UUID driverId, OrderStatus status);
}