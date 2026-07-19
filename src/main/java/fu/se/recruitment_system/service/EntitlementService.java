package fu.se.recruitment_system.service;

import fu.se.recruitment_system.model.Order;
import fu.se.recruitment_system.model.enums.BenefitType;
import fu.se.recruitment_system.service.strategy.BenefitActivationStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class EntitlementService {
    private final Map<BenefitType, BenefitActivationStrategy> strategies;

    public EntitlementService(List<BenefitActivationStrategy> strategies) {
        Map<BenefitType, BenefitActivationStrategy> registry = new EnumMap<>(BenefitType.class);
        for (BenefitActivationStrategy strategy : strategies) {
            BenefitActivationStrategy duplicate = registry.put(strategy.supportedType(), strategy);
            if (duplicate != null) {
                throw new IllegalStateException(
                        "Multiple benefit activation strategies support " + strategy.supportedType());
            }
        }
        this.strategies = Map.copyOf(registry);
    }

    public void activate(Order order) {
        BenefitType benefitType = order.getBenefitType();
        BenefitActivationStrategy strategy = strategies.get(benefitType);
        if (strategy == null) {
            throw new IllegalStateException("No benefit activation strategy supports " + benefitType);
        }

        LocalDateTime expiredAt = LocalDateTime.now()
                .plusDays(order.getServicePackage().getDurationDays());
        strategy.activate(order, expiredAt);
    }
}
