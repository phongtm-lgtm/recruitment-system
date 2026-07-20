package fu.se.recruitment_system.service.template;

import fu.se.recruitment_system.dto.RegisterRequest;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.UserRole;
import fu.se.recruitment_system.model.enums.UserStatus;
import fu.se.recruitment_system.repository.OtpVerificationRepository;
import fu.se.recruitment_system.repository.UserRepository;
import fu.se.recruitment_system.service.strategy.password.PasswordPolicyStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class RecruiterRegistrationHandler extends AbstractUserRegistrationTemplate {

    public RecruiterRegistrationHandler(UserRepository userRepository,
                                       OtpVerificationRepository otpVerificationRepository,
                                       PasswordPolicyStrategy passwordPolicyStrategy,
                                       ApplicationEventPublisher eventPublisher) {
        super(userRepository, otpVerificationRepository, passwordPolicyStrategy, eventPublisher);
    }

    @Override
    public boolean supports(UserRole role) {
        return role == UserRole.RECRUITER;
    }

    @Override
    protected void validateRoleSpecificInput(RegisterRequest request) {
        // Recruiter specific validations
    }

    @Override
    protected User buildUserEntity(RegisterRequest request) {
        return User.builder()
                .email(request.getEmail())
                .passwordHash(request.getPassword())
                .fullName(request.getFullName() != null ? request.getFullName() : request.getEmail().split("@")[0])
                .phone(request.getPhone())
                .role(UserRole.RECRUITER)
                .status(UserStatus.PENDING)
                .failedLoginAttempts(0)
                .mustChangePassword(false)
                .build();
    }
}
