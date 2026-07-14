package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
}
