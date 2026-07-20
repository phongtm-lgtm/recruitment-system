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
public class ModeratorRegistrationHandler extends AbstractUserRegistrationTemplate {

    public ModeratorRegistrationHandler(UserRepository userRepository,
                                         OtpVerificationRepository otpVerificationRepository,
                                         PasswordPolicyStrategy passwordPolicyStrategy,
                                         ApplicationEventPublisher eventPublisher) {
        super(userRepository, otpVerificationRepository, passwordPolicyStrategy, eventPublisher);
    }

    @Override
    public boolean supports(UserRole role) {
        return role == UserRole.MODERATOR;
    }

    @Override
    protected void validateRoleSpecificInput(RegisterRequest request) {
        if (request.getDepartment() == null || request.getDepartment().isBlank()) {
            throw new IllegalArgumentException("Department is mandatory for Moderator creation.");
        }
        // Corporate email check BR-1 UC-09
        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format for Moderator.");
        }
    }

    @Override
    protected User buildUserEntity(RegisterRequest request) {
        return User.builder()
                .email(request.getEmail())
                .passwordHash(request.getPassword())
                .fullName(request.getFullName() != null ? request.getFullName() : request.getEmail().split("@")[0])
                .phone(request.getPhone())
                .department(request.getDepartment())
                .permissions(request.getPermissions())
                .role(UserRole.MODERATOR)
                .status(UserStatus.ACTIVE) // Created by admin directly active
                .failedLoginAttempts(0)
                .mustChangePassword(true) // Force change password on first login (UC-09 BR-1)
                .build();
    }
}
