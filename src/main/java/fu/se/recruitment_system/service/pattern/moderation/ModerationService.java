package fu.se.recruitment_system.service.pattern.moderation;

import fu.se.recruitment_system.dto.JobPostMapper;
import fu.se.recruitment_system.dto.JobPostModerationResponse;
import fu.se.recruitment_system.dto.RecruiterJobPostResponse;
import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.JobPostStatus;
import fu.se.recruitment_system.model.enums.UserRole;
import fu.se.recruitment_system.repository.JobPostRepository;
import fu.se.recruitment_system.repository.UserRepository;
import fu.se.recruitment_system.service.pattern.audit.StatusChangeJobPostAuditLogCreator;
import fu.se.recruitment_system.service.pattern.moderation.strategy.ModerationStrategy;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ModerationService {
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;
    private final StatusChangeJobPostAuditLogCreator auditLogCreator;
    private final Map<String, ModerationStrategy> strategies;

    @Transactional(readOnly = true)
    public List<RecruiterJobPostResponse> getPendingJobPostings(Long moderatorId) {
        verifyModerator(moderatorId);
        return jobPostRepository.findTop20ByStatusOrderByCreatedAtDesc(JobPostStatus.PENDING)
                .stream()
                .map(JobPostMapper::toRecruiterResponse)
                .toList();
    }

    @Transactional
    public JobPostModerationResponse moderate(
            Long moderatorId,
            Long jobPostId,
            String decision,
            String reason) {
        User moderator = verifyModerator(moderatorId);
        ModerationStrategy strategy = strategies.get(decision == null ? null : decision.toLowerCase());
        if (strategy == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported moderation decision");
        }

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job post not found"));
        if (jobPost.getStatus() != JobPostStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Job post has already been moderated");
        }

        strategy.moderate(jobPost, reason);
        JobPost savedJobPost = jobPostRepository.save(jobPost);
        //stragegy use
        String auditMessage = "Moderator decision: " + strategy.decision();
        auditLogCreator.createAndSave(savedJobPost, moderator, auditMessage);
        return new JobPostModerationResponse(
                savedJobPost.getId(),
                savedJobPost.getStatus(),
                savedJobPost.getRejectionReason(),
                strategy.getClass().getSimpleName(),
                "JOB_POST_STATUS_CHANGED");
    }

    private User verifyModerator(Long moderatorId) {
        User moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Moderator not found"));
        if (moderator.getRole() != UserRole.MODERATOR && moderator.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Moderator permission is required");
        }
        return moderator;
    }
}
