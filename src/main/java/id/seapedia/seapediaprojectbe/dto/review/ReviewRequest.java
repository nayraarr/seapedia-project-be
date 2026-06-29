package id.seapedia.seapediaprojectbe.dto.review;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReviewRequest {
    @NotBlank(message = "Reviewer name is required")
    @Size(max = 100, message = "Reviewer name max 100 characters")
    private String reviewerName;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating minimum 1")
    @Max(value = 5, message = "Rating maximum 5")
    private Integer rating;

    @NotBlank(message = "Comment is required")
    @Size(max = 1000, message = "Comment max 1000 characters")
    private String comment;
}