package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
