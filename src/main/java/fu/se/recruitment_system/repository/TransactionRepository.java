package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.Transaction;
import fu.se.recruitment_system.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByOrderId(Long orderId);

    Optional<Transaction> findByVnpTxnRef(String vnpTxnRef);

    long countByStatusAndCreatedAtBetween(PaymentStatus status, LocalDateTime fromDate, LocalDateTime toDate);

    long countByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);

    List<Transaction> findTop10ByStatusOrderByCompletedAtDesc(PaymentStatus status);
}
