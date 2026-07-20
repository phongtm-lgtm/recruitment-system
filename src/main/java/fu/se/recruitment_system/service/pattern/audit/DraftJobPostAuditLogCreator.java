package fu.se.recruitment_system.service.pattern.audit;

import fu.se.recruitment_system.model.AuditLog;
import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.repository.AuditLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class DraftJobPostAuditLogCreator extends AbstractJobPostAuditLogCreator {
    public DraftJobPostAuditLogCreator(AuditLogRepository auditLogRepository) {
        super(auditLogRepository);
    }

    @Override
    protected AuditLog createAuditLog(JobPost jobPost, User actor, String message) {
        return newJobPostLog("JOB_POST_DRAFT_CREATED", jobPost, actor, message);
    }
}
