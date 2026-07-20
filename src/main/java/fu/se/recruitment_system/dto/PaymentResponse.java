package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.model.enums.PaymentStatus;

public record PaymentResponse(
        Long transactionId,
        String transactionReference,
        String paymentUrl,
        PaymentStatus status) {
}
