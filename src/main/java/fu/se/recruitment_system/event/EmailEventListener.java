package fu.se.recruitment_system.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailEventListener {

    @Async
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("[EMAIL SERVICE] Dispatching OTP email to {}: OTP Code is {}", 
                event.getUser().getEmail(), event.getOtpCode());
        // Simulates integration with SMTP / Mail Server
    }

    @Async
    @EventListener
    public void handlePasswordResetRequested(PasswordResetRequestedEvent event) {
        log.info("[EMAIL SERVICE] Dispatching Password Reset link to {}: Token={}", 
                event.getUser().getEmail(), event.getResetToken());
        // Simulates sending recovery link
    }
}
