package id.seapedia.seapediaprojectbe.repository;

import id.seapedia.seapediaprojectbe.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {
    boolean existsByName(String name);
    Optional<Store> findByOwnerId(UUID ownerId);
}
