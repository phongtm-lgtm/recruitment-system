package fu.se.recruitment_system.service.pattern.moderation.strategy;

import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.model.enums.JobPostStatus;
import org.springframework.stereotype.Component;

@Component
public class ApproveModerationStrategy implements ModerationStrategy {
    @Override
    public String decision() {
        return "approve";
    }

    @Override
    public void moderate(JobPost jobPost, String reason) {
        jobPost.setStatus(JobPostStatus.ACTIVE);
        jobPost.setRejectionReason(null);
    }
}
