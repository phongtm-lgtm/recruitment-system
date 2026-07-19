package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.OrderQuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderQuotaRepository extends JpaRepository<OrderQuota, Long> {
    List<OrderQuota> findByOrderRecruiterIdAndExpiredAtAfterOrderByExpiredAtAsc(
            Long recruiterId, LocalDateTime now);

    Optional<OrderQuota> findByOrderId(Long orderId);
}
