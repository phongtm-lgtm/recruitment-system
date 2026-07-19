package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.Application;
import fu.se.recruitment_system.model.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    long countByAppliedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);

    long countByStatus(ApplicationStatus status);
}
