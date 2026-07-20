package fu.se.recruitment_system.event;

import fu.se.recruitment_system.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final User user;
    private final String otpCode;

    public UserRegisteredEvent(Object source, User user, String otpCode) {
        super(source);
        this.user = user;
        this.otpCode = otpCode;
    }
}
