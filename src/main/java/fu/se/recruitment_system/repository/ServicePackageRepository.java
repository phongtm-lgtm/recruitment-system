package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
}
