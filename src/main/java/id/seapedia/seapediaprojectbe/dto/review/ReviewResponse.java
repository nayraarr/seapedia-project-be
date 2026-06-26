package id.seapedia.seapediaprojectbe.dto.review;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReviewResponse {
    private UUID id;
    private String reviewerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}