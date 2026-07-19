package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findByRecruiterIdOrderByCreatedAtDesc(Long recruiterId);

    Optional<ServiceOrder> findByIdAndRecruiterId(Long id, Long recruiterId);
}
