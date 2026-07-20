package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.enums.PaymentStatus;

public record PaymentResponse(
        Long transactionId,
        String transactionReference,
        String paymentUrl,
        PaymentStatus status) {
}
