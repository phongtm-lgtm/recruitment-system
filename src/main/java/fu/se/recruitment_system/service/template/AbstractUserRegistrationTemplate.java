package fu.se.recruitment_system.service.template;

import fu.se.recruitment_system.dto.RegisterRequest;
import fu.se.recruitment_system.event.UserRegisteredEvent;
import fu.se.recruitment_system.model.OtpVerification;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.repository.OtpVerificationRepository;
import fu.se.recruitment_system.repository.UserRepository;
import fu.se.recruitment_system.service.strategy.password.PasswordPolicyStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@RequiredArgsConstructor
public abstract class AbstractUserRegistrationTemplate {

    protected final UserRepository userRepository;
    protected final OtpVerificationRepository otpVerificationRepository;
    protected final PasswordPolicyStrategy passwordPolicyStrategy;
    protected final ApplicationEventPublisher eventPublisher;

    // Template Method defining the registration skeleton
    @Transactional
    public User register(RegisterRequest request) {
        validateCommonInput(request);
        validateRoleSpecificInput(request);
        checkDuplicates(request);

        User user = buildUserEntity(request);
        passwordPolicyStrategy.recordPasswordInHistory(user, user.getPasswordHash());
        user = userRepository.save(user);

        String otpCode = generateOtpCode();
        createOtpVerification(user, otpCode);

        publishRegistrationEvent(user, otpCode);
        postProcess(user);

        return user;
    }

    protected void validateCommonInput(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        passwordPolicyStrategy.validatePasswordStrength(request.getPassword());
    }

    protected abstract void validateRoleSpecificInput(RegisterRequest request);

    protected void checkDuplicates(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        if (request.getPhone() != null && !request.getPhone().isBlank() && userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists.");
        }
    }

    protected abstract User buildUserEntity(RegisterRequest request);

    protected void createOtpVerification(User user, String otpCode) {
        OtpVerification otp = OtpVerification.builder()
                .user(user)
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .isUsed(false)
                .build();
        otpVerificationRepository.save(otp);
    }

    protected void publishRegistrationEvent(User user, String otpCode) {
        eventPublisher.publishEvent(new UserRegisteredEvent(this, user, otpCode));
    }

    protected String generateOtpCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    protected void postProcess(User user) {
        // Default hook (can be overridden by subclasses)
    }

    public abstract boolean supports(fu.se.recruitment_system.model.enums.UserRole role);
}
