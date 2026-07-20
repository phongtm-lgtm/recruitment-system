package fu.se.recruitment_system.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ModeratorTaskReassignmentListener {

    @Async
    @EventListener
    public void handleModeratorDeactivated(ModeratorDeactivatedEvent event) {
        log.info("[MODERATION SERVICE] Moderator {} deactivated. Reason: {}. Reassigning pending tasks back to general pool.",
                event.getModerator().getEmail(), event.getReason());
        // Simulates scanning and returning pending job posting tasks to unassigned queue
    }
}
