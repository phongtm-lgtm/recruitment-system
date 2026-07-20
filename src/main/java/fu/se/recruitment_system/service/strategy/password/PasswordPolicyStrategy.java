package fu.se.recruitment_system.service.strategy.password;

import fu.se.recruitment_system.model.PasswordHistory;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.repository.PasswordHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class PasswordPolicyStrategy {

    private final PasswordHistoryRepository passwordHistoryRepository;

    // BR-1: Passwords must be at least 8 characters long, including uppercase, lowercase, and numbers.
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

    public void validatePasswordStrength(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain uppercase letters, lowercase letters, and numbers.");
        }
    }

    public void validatePasswordHistory(User user, String newPassword) {
        // Fetch last 3 passwords for the user (BR-1 UC-06)
        List<PasswordHistory> recentHistories = passwordHistoryRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(0, 3));
        
        for (PasswordHistory history : recentHistories) {
            if (matches(newPassword, history.getPasswordHash())) {
                throw new IllegalArgumentException("Cannot reuse any of your last 3 passwords.");
            }
        }
    }

    public void recordPasswordInHistory(User user, String passwordHash) {
        PasswordHistory history = PasswordHistory.builder()
                .user(user)
                .passwordHash(passwordHash)
                .build();
        passwordHistoryRepository.save(history);
    }

    private boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) return false;
        return rawPassword.equals(storedHash) || storedHash.equals("HASH_" + rawPassword);
    }
}
