package fu.se.recruitment_system.service.strategy.auth;

import fu.se.recruitment_system.dto.AuthRequest;
import fu.se.recruitment_system.dto.AuthResponse;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.UserStatus;
import fu.se.recruitment_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LocalAuthStrategy implements AuthenticationStrategy {

    private final UserRepository userRepository;

    @Override
    public boolean supports(String provider) {
        return "LOCAL".equalsIgnoreCase(provider) || provider == null;
    }

    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Incorrect email or password"));

        // Check account status: INACTIVE
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new IllegalStateException("Your account has been deactivated by the Administrator.");
        }

        // Check lockout status (UC-02 BR-1)
        if (user.getStatus() == UserStatus.LOCKED || user.getLockoutTime() != null) {
            if (user.getLockoutTime() != null && user.getLockoutTime().isAfter(LocalDateTime.now())) {
                throw new IllegalStateException("Too many failed attempts. Account locked for 15 minutes.");
            } else {
                // Lock period expired, unlock account
                user.setStatus(UserStatus.ACTIVE);
                user.setLockoutTime(null);
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
            }
        }

        // Simple password check (In production/Spring SecurityBCrypt, here simulated via plain/hash match)
        boolean passwordMatches = checkPasswordMatch(request.getPassword(), user.getPasswordHash());

        if (!passwordMatches) {
            int attempts = (user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0) + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= 5) {
                user.setStatus(UserStatus.LOCKED);
                user.setLockoutTime(LocalDateTime.now().plusMinutes(15));
                userRepository.save(user);
                throw new IllegalStateException("Too many failed attempts. Account locked for 15 minutes.");
            }
            userRepository.save(user);
            throw new IllegalArgumentException("Incorrect email or password");
        }

        // Check pending status
        if (user.getStatus() == UserStatus.PENDING) {
            throw new IllegalStateException("Account is pending OTP verification. Please verify your email.");
        }

        // Success: Reset failed login counters
        user.setFailedLoginAttempts(0);
        user.setLockoutTime(null);
        userRepository.save(user);

        // Generate token session string
        String token = "JWT_SESSION_TOKEN_" + user.getId() + "_" + System.currentTimeMillis();

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .mustChangePassword(user.getMustChangePassword())
                .message("Login successful")
                .build();
    }

    private boolean checkPasswordMatch(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) return false;
        // Allows direct equality or hash matching
        return rawPassword.equals(storedHash) || storedHash.equals("HASH_" + rawPassword);
    }
}
