package fu.se.recruitment_system.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    private final Set<Long> invalidatedUserIds = ConcurrentHashMap.newKeySet();

    public void blacklistToken(String token) {
        if (token != null && !token.isBlank()) {
            blacklistedTokens.add(token);
        }
    }

    public void invalidateUserSessions(Long userId) {
        if (userId != null) {
            invalidatedUserIds.add(userId);
        }
    }

    public boolean isBlacklisted(String token) {
        if (token == null) return false;
        return blacklistedTokens.contains(token);
    }

    public boolean isUserSessionInvalidated(Long userId) {
        if (userId == null) return false;
        return invalidatedUserIds.contains(userId);
    }

    public void clearUserSessionInvalidation(Long userId) {
        if (userId != null) {
            invalidatedUserIds.remove(userId);
        }
    }
}
