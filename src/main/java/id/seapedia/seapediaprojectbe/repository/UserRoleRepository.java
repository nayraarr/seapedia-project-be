package id.seapedia.seapediaprojectbe.repository;

import id.seapedia.seapediaprojectbe.model.RoleType;
import id.seapedia.seapediaprojectbe.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    List<UserRole> findByUserId(UUID userId);
    boolean existsByUserIdAndRole(UUID userId, RoleType role);
    void deleteByUserIdAndRole(UUID userId, RoleType role);
    long countByRole(RoleType role);
}
