package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
