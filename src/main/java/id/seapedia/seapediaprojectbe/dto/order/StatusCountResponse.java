package id.seapedia.seapediaprojectbe.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusCountResponse {
    private String status;
    private String statusLabel;
    private Integer count;
}