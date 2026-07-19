package fu.se.recruitment_system.controller;

import fu.se.recruitment_system.dto.PaymentResponse;
import fu.se.recruitment_system.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}")
    public PaymentResponse createPayment(
            @RequestHeader("X-Recruiter-Id") Long recruiterId,
            @PathVariable Long orderId) {
        return paymentService.createPayment(recruiterId, orderId);
    }

    @GetMapping("/demo-return")
    public PaymentResponse handleDemoReturn(
            @RequestParam String vnpTxnRef,
            @RequestParam(defaultValue = "true") boolean success) {
        return paymentService.completePayment(vnpTxnRef, success);
    }
}
