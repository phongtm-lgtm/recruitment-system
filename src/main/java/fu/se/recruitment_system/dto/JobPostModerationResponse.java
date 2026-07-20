package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.model.enums.JobPostStatus;

public record JobPostModerationResponse(
        Long jobPostId,
        JobPostStatus status,
        String rejectionReason,
        String strategy,
        String auditAction) {
}
