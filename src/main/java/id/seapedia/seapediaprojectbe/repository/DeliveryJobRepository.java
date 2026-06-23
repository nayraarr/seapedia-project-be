package id.seapedia.seapediaprojectbe.repository;

import id.seapedia.seapediaprojectbe.model.DeliveryJob;
import id.seapedia.seapediaprojectbe.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryJobRepository extends JpaRepository<DeliveryJob, UUID> {
    Optional<DeliveryJob> findByOrderId(UUID orderId);

    List<DeliveryJob> findByOrder_StatusOrderByCreatedAtAsc(OrderStatus status);

    Optional<DeliveryJob> findByIdAndOrder_Status(UUID id, OrderStatus status);
}