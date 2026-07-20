package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {
    long countByRole(UserRole role);

    long countByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
