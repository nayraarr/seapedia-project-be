package id.seapedia.seapediaprojectbe.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private Long price;
    private Integer stock;
    private String imageUrl;
    private Integer soldCount;
    private UUID storeId;
    private String storeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
