package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
    List<ServicePackage> findByActiveTrueOrderByPriceAsc();

    Optional<ServicePackage> findByIdAndActiveTrue(Long id);

    boolean existsByPackageName(String packageName);

    boolean existsByPackageNameAndIdNot(String packageName, Long id);
}
