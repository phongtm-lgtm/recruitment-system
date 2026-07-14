package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
}
