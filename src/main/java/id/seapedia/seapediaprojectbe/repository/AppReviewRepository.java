package id.seapedia.seapediaprojectbe.repository;

import id.seapedia.seapediaprojectbe.model.AppReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppReviewRepository extends JpaRepository<AppReview, UUID> {
    List<AppReview> findAllByOrderByCreatedAtDesc();
}
