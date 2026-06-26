package id.seapedia.seapediaprojectbe.service;

import id.seapedia.seapediaprojectbe.dto.admin.OverdueProcessResult;
import java.util.List;

public interface OverdueService {
    List<OverdueProcessResult> processAllOverdueOrders();
    OverdueProcessResult processOverdueOrder(java.util.UUID orderId);
}