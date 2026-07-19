package fu.se.recruitment_system.service;

import fu.se.recruitment_system.dto.SubscriptionResponse;
import fu.se.recruitment_system.model.OrderQuota;
import fu.se.recruitment_system.model.ServiceOrder;
import fu.se.recruitment_system.repository.OrderQuotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EntitlementService {
    private final OrderQuotaRepository quotaRepository;
    private final JobPostService jobPostService;

    public EntitlementService(OrderQuotaRepository quotaRepository, JobPostService jobPostService) {
        this.quotaRepository = quotaRepository;
        this.jobPostService = jobPostService;
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public void activateBenefit(ServiceOrder order) {
        LocalDateTime expiredAt = LocalDateTime.now()
                .plusDays(order.getServicePackage().getDurationDays());

        if (order.getJobPost() != null) {
            jobPostService.activateFeaturedJob(order.getJobPost(), expiredAt);
            return;
        }

        if (quotaRepository.findByOrderId(order.getId()).isPresent()) {
            return;
        }

        OrderQuota quota = new OrderQuota();
        quota.setOrder(order);
        quota.setPostLeft(order.getServicePackage().getQuota());
        quota.setExpiredAt(expiredAt);
        quotaRepository.save(quota);
    }
}
