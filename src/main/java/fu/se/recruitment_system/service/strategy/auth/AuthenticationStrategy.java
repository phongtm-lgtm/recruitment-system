package fu.se.recruitment_system.service.strategy.auth;

import fu.se.recruitment_system.dto.AuthRequest;
import fu.se.recruitment_system.dto.AuthResponse;

public interface AuthenticationStrategy {
    boolean supports(String provider);
    AuthResponse authenticate(AuthRequest request);
}
