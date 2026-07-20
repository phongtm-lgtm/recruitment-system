package fu.se.recruitment_system.service;

import fu.se.recruitment_system.dto.PaymentResponse;
import fu.se.recruitment_system.model.Transaction;
import fu.se.recruitment_system.model.Order;
import fu.se.recruitment_system.model.enums.PaymentStatus;
import fu.se.recruitment_system.model.enums.OrderStatus;
import fu.se.recruitment_system.repository.TransactionRepository;
import fu.se.recruitment_system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final EntitlementService entitlementService;
    private final PaymentGateway paymentGateway;

    @Transactional
    public PaymentResponse createPayment(Long recruiterId, Long orderId) {
        Order order = orderService.getOwnedOrder(recruiterId, orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(CONFLICT, "Only pending orders can be paid");
        }

        Transaction transaction = transactionRepository.findByOrderId(orderId)
                .orElseGet(() -> createPendingTransaction(order));
        if (transaction.getStatus() != PaymentStatus.PENDING) {
            throw new ResponseStatusException(CONFLICT, "Payment transaction is already completed");
        }

        return toResponse(transaction, paymentGateway.createPaymentUrl(transaction));
    }

    @Transactional
    public PaymentResponse completePayment(String transactionReference, boolean success) {
        Transaction transaction = transactionRepository.findByVnpTxnRef(transactionReference)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment transaction not found"));

        if (transaction.getStatus() != PaymentStatus.PENDING) {
            return toResponse(transaction, null);
        }

        if (!paymentGateway.verifyCallback(transactionReference, success)) {
            throw new ResponseStatusException(CONFLICT, "Invalid payment callback");
        }

        Order order = transaction.getOrder();
        transaction.setCompletedAt(LocalDateTime.now());
        transaction.setProviderReference("DEMO-" + transactionReference);

        if (success) {
            transaction.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
            entitlementService.activate(order);
        } else {
            transaction.setStatus(PaymentStatus.FAILED);
            transaction.setFailureReason("Demo payment was declined");
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        transactionRepository.save(transaction);
        return toResponse(transaction, null);
    }

    private Transaction createPendingTransaction(Order order) {
        Transaction transaction = new Transaction();
        transaction.setOrder(order);
        transaction.setVnpTxnRef(UUID.randomUUID().toString());
        transaction.setPaymentGateway("VNPAY_DEMO");
        transaction.setStatus(PaymentStatus.PENDING);
        transaction.setAmount(order.getAmount());
        return transactionRepository.save(transaction);
    }

    private PaymentResponse toResponse(Transaction transaction, String paymentUrl) {
        return new PaymentResponse(
                transaction.getId(),
                transaction.getVnpTxnRef(),
                paymentUrl,
                transaction.getStatus());
    }
}
