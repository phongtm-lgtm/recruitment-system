package fu.se.recruitment_system.service;

import fu.se.recruitment_system.dto.JobPostResponse;
import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.model.enums.JobPostStatus;
import fu.se.recruitment_system.repository.JobPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class JobPostService {
    private final JobPostRepository jobPostRepository;

    public JobPostService(JobPostRepository jobPostRepository) {
        this.jobPostRepository = jobPostRepository;
    }

    public List<JobPostResponse> getEligibleJobs(Long recruiterId) {
        return jobPostRepository
                .findByRecruiterIdAndStatusOrderByCreatedAtDesc(recruiterId, JobPostStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public JobPost getEligibleJob(Long recruiterId, Long jobPostId) {
        return jobPostRepository
                .findByIdAndRecruiterIdAndStatus(jobPostId, recruiterId, JobPostStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Eligible job post not found"));
    }

    @Transactional
    public void activateFeaturedJob(JobPost jobPost, LocalDateTime expiredAt) {
        jobPost.setFeatured(true);
        jobPost.setFeatureExpireAt(expiredAt);
        jobPostRepository.save(jobPost);
    }

    private JobPostResponse toResponse(JobPost jobPost) {
        return new JobPostResponse(
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getStatus(),
                jobPost.isFeatured(),
                jobPost.getFeatureExpireAt());
    }
}
