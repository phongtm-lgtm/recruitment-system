package fu.se.recruitment_system.service.pattern.moderation.strategy;

import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.model.enums.JobPostStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class RejectModerationStrategy implements ModerationStrategy {
    @Override
    public String decision() {
        return "reject";
    }

    @Override
    public void moderate(JobPost jobPost, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rejection reason is required");
        }
        jobPost.setStatus(JobPostStatus.REJECTED);
        jobPost.setRejectionReason(reason.trim());
    }
}
