package fu.se.recruitment_system.event;

import fu.se.recruitment_system.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PasswordResetRequestedEvent extends ApplicationEvent {
    private final User user;
    private final String resetToken;

    public PasswordResetRequestedEvent(Object source, User user, String resetToken) {
        super(source);
        this.user = user;
        this.resetToken = resetToken;
    }
}
