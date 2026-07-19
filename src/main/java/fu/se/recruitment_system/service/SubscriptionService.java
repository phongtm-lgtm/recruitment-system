package fu.se.recruitment_system.service;

import fu.se.recruitment_system.dto.SubscriptionResponse;
import fu.se.recruitment_system.repository.OrderQuotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SubscriptionService {
    private final OrderQuotaRepository quotaRepository;

    public SubscriptionService(OrderQuotaRepository quotaRepository) {
        this.quotaRepository = quotaRepository;
    }

    public List<SubscriptionResponse> getActiveSubscriptions(Long recruiterId) {
        return quotaRepository
                .findByOrderRecruiterIdAndExpiredAtAfterOrderByExpiredAtAsc(recruiterId, LocalDateTime.now())
                .stream()
                .map(quota -> new SubscriptionResponse(
                        quota.getOrder().getId(),
                        quota.getOrder().getServicePackage().getPackageName(),
                        quota.getPostLeft(),
                        quota.getExpiredAt()))
                .toList();
    }
}
