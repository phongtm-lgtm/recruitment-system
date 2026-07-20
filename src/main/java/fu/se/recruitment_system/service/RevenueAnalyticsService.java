package fu.se.recruitment_system.service;

import fu.se.recruitment_system.model.Transaction;
import fu.se.recruitment_system.enums.OrderStatus;
import fu.se.recruitment_system.enums.PaymentStatus;
import fu.se.recruitment_system.repository.OrderRepository;
import fu.se.recruitment_system.repository.ServicePackageRepository;
import fu.se.recruitment_system.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RevenueAnalyticsService {
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final ServicePackageRepository servicePackageRepository;

    public RevenueAnalyticsService(
            OrderRepository orderRepository,
            TransactionRepository transactionRepository,
            ServicePackageRepository servicePackageRepository) {
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
        this.servicePackageRepository = servicePackageRepository;
    }

    public Map<String, Object> getRevenueStatistics(LocalDateTime fromDate, LocalDateTime toDate) {
        return Map.of(
                "totalRevenue", orderRepository.sumPaidOrderAmount(fromDate, toDate),
                "paidOrders", orderRepository.countByStatusAndCreatedAtBetween(OrderStatus.PAID, fromDate, toDate),
                "paymentSuccessRate", getPaymentSuccessRate(fromDate, toDate));
    }

    public BigDecimal getPaymentSuccessRate(LocalDateTime fromDate, LocalDateTime toDate) {
        long totalTransactions = transactionRepository.countByCreatedAtBetween(fromDate, toDate);
        if (totalTransactions == 0) {
            return BigDecimal.ZERO;
        }
        long successfulTransactions = transactionRepository.countByStatusAndCreatedAtBetween(
                PaymentStatus.SUCCESS, fromDate, toDate);
        return BigDecimal.valueOf(successfulTransactions)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP);
    }

    public Map<String, Object> getRevenueTrend(LocalDateTime fromDate, LocalDateTime toDate) {
        return Map.of("fromDate", fromDate, "toDate", toDate, "revenue", orderRepository.sumPaidOrderAmount(fromDate, toDate));
    }

    public Map<String, Object> getPackageRevenueBreakdown(LocalDateTime fromDate, LocalDateTime toDate) {
        return Map.of(
                "packageCount", servicePackageRepository.count(),
                "paidOrders", orderRepository.countByStatusAndCreatedAtBetween(OrderStatus.PAID, fromDate, toDate),
                "revenue", orderRepository.sumPaidOrderAmount(fromDate, toDate));
    }

    public List<Transaction> getRecentSuccessfulTransactions() {
        return transactionRepository.findTop10ByStatusOrderByCompletedAtDesc(PaymentStatus.SUCCESS);
    }
}
