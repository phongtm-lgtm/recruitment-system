package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.PasswordHistory;
import fu.se.recruitment_system.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    List<PasswordHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
