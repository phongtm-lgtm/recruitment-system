package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.OtpVerification;
import fu.se.recruitment_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findTopByUserAndIsUsedFalseOrderByCreatedAtDesc(User user);
    Optional<OtpVerification> findTopByUserAndOtpCodeAndIsUsedFalseOrderByCreatedAtDesc(User user, String otpCode);
}
