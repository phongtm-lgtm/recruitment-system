package fu.se.recruitment_system.service.strategy.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthStrategyFactory {

    private final List<AuthenticationStrategy> strategies;

    public AuthenticationStrategy getStrategy(String provider) {
        String targetProvider = (provider == null || provider.isBlank()) ? "LOCAL" : provider;
        return strategies.stream()
                .filter(s -> s.supports(targetProvider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported authentication provider: " + provider));
    }
}
