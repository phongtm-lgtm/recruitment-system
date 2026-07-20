package fu.se.recruitment_system.service;

import fu.se.recruitment_system.dto.AuthRequest;
import fu.se.recruitment_system.dto.AuthResponse;
import fu.se.recruitment_system.dto.RegisterRequest;
import fu.se.recruitment_system.event.PasswordResetRequestedEvent;
import fu.se.recruitment_system.event.UserRegisteredEvent;
import fu.se.recruitment_system.model.OtpVerification;
import fu.se.recruitment_system.model.PasswordResetToken;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.UserRole;
import fu.se.recruitment_system.model.enums.UserStatus;
import fu.se.recruitment_system.repository.OtpVerificationRepository;
import fu.se.recruitment_system.repository.PasswordResetTokenRepository;
import fu.se.recruitment_system.repository.UserRepository;
import fu.se.recruitment_system.service.strategy.auth.AuthStrategyFactory;
import fu.se.recruitment_system.service.strategy.password.PasswordPolicyStrategy;
import fu.se.recruitment_system.service.template.AbstractUserRegistrationTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final List<AbstractUserRegistrationTemplate> registrationTemplates;
    private final AuthStrategyFactory authStrategyFactory;
    private final UserRepository userRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordPolicyStrategy passwordPolicyStrategy;
    private final TokenBlacklistService tokenBlacklistService;
    private final ApplicationEventPublisher eventPublisher;

    // UC-01: Register Account
    @Transactional
    public User register(RegisterRequest request) {
        UserRole targetRole = request.getRole() != null ? request.getRole() : UserRole.JOB_SEEKER;
        AbstractUserRegistrationTemplate template = registrationTemplates.stream()
                .filter(t -> t.supports(targetRole))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported registration role: " + targetRole));

        return template.register(request);
    }

    // UC-01: Verify OTP Code
    @Transactional
    public void verifyOtp(String email, String otpCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OtpVerification otp = otpVerificationRepository
                .findTopByUserAndOtpCodeAndIsUsedFalseOrderByCreatedAtDesc(user, otpCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP code"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP code has expired. Please request a new OTP.");
        }

        otp.setIsUsed(true);
        otpVerificationRepository.save(otp);

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    // UC-01 Alt 6.1: Resend OTP
    @Transactional
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalStateException("Account is already activated.");
        }

        String newOtpCode = String.format("%06d", new Random().nextInt(1000000));

        OtpVerification otp = OtpVerification.builder()
                .user(user)
                .otpCode(newOtpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .isUsed(false)
                .build();
        otpVerificationRepository.save(otp);

        eventPublisher.publishEvent(new UserRegisteredEvent(this, user, newOtpCode));
    }

    // UC-02 & UC-03: Authenticate (Password or Google Auth Strategy)
    public AuthResponse authenticate(AuthRequest request) {
        return authStrategyFactory.getStrategy(request.getProvider()).authenticate(request);
    }

    // UC-04: Recover Password (Anti-enumeration protection)
    @Transactional
    public String requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && userOpt.get().getStatus() != UserStatus.INACTIVE) {
            User user = userOpt.get();
            String resetToken = UUID.randomUUID().toString();

            PasswordResetToken tokenEntity = PasswordResetToken.builder()
                    .user(user)
                    .token(resetToken)
                    .expiresAt(LocalDateTime.now().plusMinutes(15)) // 15 mins expiry
                    .isUsed(false)
                    .build();
            passwordResetTokenRepository.save(tokenEntity);

            eventPublisher.publishEvent(new PasswordResetRequestedEvent(this, user, resetToken));
        }

        // Anti-enumeration return message regardless of whether email exists (UC-04 Alt 4.1)
        return "If your email is registered, a recovery link has been sent.";
    }

    // UC-04: Perform Password Reset with Token
    @Transactional
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset link."));

        if (resetToken.getIsUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired password reset link.");
        }

        User user = resetToken.getUser();
        passwordPolicyStrategy.validatePasswordStrength(newPassword);
        passwordPolicyStrategy.validatePasswordHistory(user, newPassword);

        user.setPasswordHash(newPassword);
        user.setMustChangePassword(false);
        userRepository.save(user);

        passwordPolicyStrategy.recordPasswordInHistory(user, newPassword);

        resetToken.setIsUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    // UC-05: Logout
    public void logout(String token) {
        tokenBlacklistService.blacklistToken(token);
    }

    // UC-06: Change Password
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New passwords do not match.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!currentPassword.equals(user.getPasswordHash()) && !"HASH_".concat(currentPassword).equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect current password.");
        }

        passwordPolicyStrategy.validatePasswordStrength(newPassword);
        passwordPolicyStrategy.validatePasswordHistory(user, newPassword); // Check last 3 passwords (BR-1 UC-06)

        user.setPasswordHash(newPassword);
        user.setMustChangePassword(false);
        userRepository.save(user);

        passwordPolicyStrategy.recordPasswordInHistory(user, newPassword);
    }
}
