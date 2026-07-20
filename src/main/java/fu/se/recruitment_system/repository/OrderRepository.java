package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.enums.OrderStatus;
import fu.se.recruitment_system.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRecruiterIdOrderByCreatedAtDesc(Long recruiterId);

    Optional<Order> findByIdAndRecruiterId(Long id, Long recruiterId);

    boolean existsByServicePackageId(Long packageId);

    long countByServicePackageId(Long packageId);

    long countByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime fromDate, LocalDateTime toDate);

    @Query(value = "select coalesce(sum(amount), 0) from service_orders where status = 'PAID' and created_at between :fromDate and :toDate", nativeQuery = true)
    BigDecimal sumPaidOrderAmount(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}
