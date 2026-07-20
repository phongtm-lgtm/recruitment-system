package fu.se.recruitment_system.service.strategy.auth;

import fu.se.recruitment_system.dto.AuthRequest;
import fu.se.recruitment_system.dto.AuthResponse;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.UserRole;
import fu.se.recruitment_system.model.enums.UserStatus;
import fu.se.recruitment_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoogleAuthStrategy implements AuthenticationStrategy {

    private final UserRepository userRepository;

    @Override
    public boolean supports(String provider) {
        return "GOOGLE".equalsIgnoreCase(provider);
    }

    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        String email = request.getEmail();
        String googleId = request.getGoogleId() != null ? request.getGoogleId() : "GOOGLE_ID_" + email;

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Google authentication failed: Email is required.");
        }

        Optional<User> existingUserOpt = userRepository.findByEmail(email);
        User user;

        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            // Check if account is inactive
            if (user.getStatus() == UserStatus.INACTIVE) {
                throw new IllegalStateException("Your account has been deactivated by the Administrator.");
            }
            // Auto-Activation if PENDING (UC-03 ALT 5.2)
            if (user.getStatus() == UserStatus.PENDING) {
                user.setStatus(UserStatus.ACTIVE);
            }
            // Account Linking (UC-03 Step 5)
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
            }
            userRepository.save(user);
        } else {
            // Auto-Registration (UC-03 ALT 5.1)
            user = User.builder()
                    .email(email)
                    .fullName(email.split("@")[0])
                    .googleId(googleId)
                    .role(UserRole.JOB_SEEKER)
                    .status(UserStatus.ACTIVE)
                    .failedLoginAttempts(0)
                    .mustChangePassword(false)
                    .build();
            userRepository.save(user);
        }

        String token = "JWT_GOOGLE_SESSION_TOKEN_" + user.getId() + "_" + System.currentTimeMillis();

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .mustChangePassword(user.getMustChangePassword())
                .message("Google authentication successful")
                .build();
    }
}
