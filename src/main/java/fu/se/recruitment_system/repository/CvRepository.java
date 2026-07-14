package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.Cv;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CvRepository extends JpaRepository<Cv, Long> {
}
