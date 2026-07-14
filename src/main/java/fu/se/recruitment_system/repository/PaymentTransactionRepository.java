package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
}
