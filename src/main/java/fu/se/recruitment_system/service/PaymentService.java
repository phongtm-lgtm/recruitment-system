package fu.se.recruitment_system.service;

import fu.se.recruitment_system.dto.PaymentResponse;
import fu.se.recruitment_system.model.PaymentTransaction;
import fu.se.recruitment_system.model.ServiceOrder;
import fu.se.recruitment_system.model.enums.PaymentStatus;
import fu.se.recruitment_system.model.enums.ServiceOrderStatus;
import fu.se.recruitment_system.repository.PaymentTransactionRepository;
import fu.se.recruitment_system.repository.ServiceOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PaymentService {
    private final PaymentTransactionRepository transactionRepository;
    private final ServiceOrderRepository orderRepository;
    private final OrderService orderService;
    private final EntitlementService entitlementService;
    private final PaymentGatewayAdapter gatewayAdapter;

    public PaymentService(
            PaymentTransactionRepository transactionRepository,
            ServiceOrderRepository orderRepository,
            OrderService orderService,
            EntitlementService entitlementService,
            PaymentGatewayAdapter gatewayAdapter) {
        this.transactionRepository = transactionRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.entitlementService = entitlementService;
        this.gatewayAdapter = gatewayAdapter;
    }

    @Transactional
    public PaymentResponse createPayment(Long recruiterId, Long orderId) {
        ServiceOrder order = orderService.getOwnedOrder(recruiterId, orderId);
        if (order.getStatus() != ServiceOrderStatus.PENDING) {
            throw new ResponseStatusException(CONFLICT, "Only pending orders can be paid");
        }

        PaymentTransaction transaction = transactionRepository.findByOrderId(orderId)
                .orElseGet(() -> createPendingTransaction(order));
        if (transaction.getStatus() != PaymentStatus.PENDING) {
            throw new ResponseStatusException(CONFLICT, "Payment transaction is already completed");
        }

        return toResponse(transaction, gatewayAdapter.createPaymentUrl(transaction));
    }

    @Transactional
    public PaymentResponse completePayment(String transactionReference, boolean success) {
        PaymentTransaction transaction = transactionRepository.findByVnpTxnRef(transactionReference)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment transaction not found"));

        if (transaction.getStatus() != PaymentStatus.PENDING) {
            return toResponse(transaction, null);
        }

        ServiceOrder order = transaction.getOrder();
        transaction.setCompletedAt(LocalDateTime.now());
        transaction.setProviderReference("DEMO-" + transactionReference);

        if (success) {
            transaction.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(ServiceOrderStatus.PAID);
            orderRepository.save(order);
            entitlementService.activateBenefit(order);
        } else {
            transaction.setStatus(PaymentStatus.FAILED);
            transaction.setFailureReason("Demo payment was declined");
            order.setStatus(ServiceOrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        transactionRepository.save(transaction);
        return toResponse(transaction, null);
    }

    private PaymentTransaction createPendingTransaction(ServiceOrder order) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrder(order);
        transaction.setVnpTxnRef(UUID.randomUUID().toString());
        transaction.setPaymentGateway("VNPAY_DEMO");
        transaction.setStatus(PaymentStatus.PENDING);
        transaction.setAmount(order.getAmount());
        return transactionRepository.save(transaction);
    }

    private PaymentResponse toResponse(PaymentTransaction transaction, String paymentUrl) {
        return new PaymentResponse(
                transaction.getId(),
                transaction.getVnpTxnRef(),
                paymentUrl,
                transaction.getStatus());
    }
}
