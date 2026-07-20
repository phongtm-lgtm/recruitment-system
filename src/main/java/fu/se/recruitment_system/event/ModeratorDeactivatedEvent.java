package fu.se.recruitment_system.event;

import fu.se.recruitment_system.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ModeratorDeactivatedEvent extends ApplicationEvent {
    private final User moderator;
    private final String reason;

    public ModeratorDeactivatedEvent(Object source, User moderator, String reason) {
        super(source);
        this.moderator = moderator;
        this.reason = reason;
    }
}
