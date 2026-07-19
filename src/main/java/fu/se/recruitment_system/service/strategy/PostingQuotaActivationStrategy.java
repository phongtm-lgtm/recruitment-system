package fu.se.recruitment_system.service.strategy;

import fu.se.recruitment_system.model.OrderQuota;
import fu.se.recruitment_system.model.Order;
import fu.se.recruitment_system.model.enums.BenefitType;
import fu.se.recruitment_system.repository.OrderQuotaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostingQuotaActivationStrategy implements BenefitActivationStrategy {
    private final OrderQuotaRepository quotaRepository;

    public PostingQuotaActivationStrategy(OrderQuotaRepository quotaRepository) {
        this.quotaRepository = quotaRepository;
    }

    @Override
    public BenefitType supportedType() {
        return BenefitType.POSTING_QUOTA;
    }

    @Override
    public void activate(Order order, LocalDateTime expiredAt) {
        if (order.getJobPost() != null) {
            throw new IllegalArgumentException("Posting quota order must not target a job post");
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
