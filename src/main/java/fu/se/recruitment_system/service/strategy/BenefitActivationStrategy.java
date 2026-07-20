package fu.se.recruitment_system.service.strategy;

import fu.se.recruitment_system.model.Order;
import fu.se.recruitment_system.enums.BenefitType;

import java.time.LocalDateTime;

public interface BenefitActivationStrategy {
    BenefitType supportedType();

    void activate(Order order, LocalDateTime expiredAt);
}
