package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long orderId,
        String packageName,
        Long jobPostId,
        BigDecimal amount,
        OrderStatus status,
        LocalDateTime createdAt) {
}
