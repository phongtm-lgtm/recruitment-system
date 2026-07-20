package fu.se.recruitment_system.service.pattern.audit;

import fu.se.recruitment_system.model.AuditLog;
import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.repository.AuditLogRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractJobPostAuditLogCreator {
    private final AuditLogRepository auditLogRepository;

    public void createAndSave(JobPost jobPost, User actor, String message) {
        AuditLog auditLog = createAuditLog(jobPost, actor, message);
        auditLogRepository.save(auditLog);
    }

    protected abstract AuditLog createAuditLog(JobPost jobPost, User actor, String message);

    protected AuditLog newJobPostLog(String action, JobPost jobPost, User actor, String message) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityType("JobPost");
        auditLog.setEntityId(jobPost.getId());
        auditLog.setActor(actor);
        auditLog.setMessage(message);
        return auditLog;
    }
}
