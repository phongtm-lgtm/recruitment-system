package fu.se.recruitment_system.dto;

import java.time.LocalDateTime;

public record SubscriptionResponse(
        Long orderId,
        String packageName,
        int postLeft,
        LocalDateTime expiredAt) {
}
