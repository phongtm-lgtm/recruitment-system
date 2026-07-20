package fu.se.recruitment_system.controller;

import fu.se.recruitment_system.dto.JobPostModerationResponse;
import fu.se.recruitment_system.dto.ModerationDecisionRequest;
import fu.se.recruitment_system.dto.RecruiterJobPostResponse;
import fu.se.recruitment_system.service.pattern.moderation.ModerationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/moderator/jobs")
public class ModeratorJobController {
    private final ModerationService moderationService;

    public ModeratorJobController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @GetMapping("/pending")
    public List<RecruiterJobPostResponse> getPendingJobPostings(
            @RequestHeader("X-Moderator-Id") Long moderatorId) {
        return moderationService.getPendingJobPostings(moderatorId);
    }

    @PatchMapping("/{jobPostId}/approve")
    public JobPostModerationResponse approve(
            @RequestHeader("X-Moderator-Id") Long moderatorId,
            @PathVariable Long jobPostId) {
        return moderationService.moderate(moderatorId, jobPostId, "approve", null);
    }

    @PatchMapping("/{jobPostId}/reject")
    public JobPostModerationResponse reject(
            @RequestHeader("X-Moderator-Id") Long moderatorId,
            @PathVariable Long jobPostId,
            @RequestBody ModerationDecisionRequest request) {
        return moderationService.moderate(moderatorId, jobPostId, "reject", request.reason());
    }

    @PatchMapping("/{jobPostId}/escalate")
    public JobPostModerationResponse escalate(
            @RequestHeader("X-Moderator-Id") Long moderatorId,
            @PathVariable Long jobPostId,
            @RequestBody(required = false) ModerationDecisionRequest request) {
        return moderationService.moderate(
                moderatorId,
                jobPostId,
                "escalate",
                request == null ? null : request.reason());
    }
}
