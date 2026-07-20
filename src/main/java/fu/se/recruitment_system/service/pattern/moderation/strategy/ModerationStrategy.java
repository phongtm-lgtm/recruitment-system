package fu.se.recruitment_system.service.pattern.moderation.strategy;

import fu.se.recruitment_system.model.JobPost;

public interface ModerationStrategy {
    String decision();

    void moderate(JobPost jobPost, String reason);
}
