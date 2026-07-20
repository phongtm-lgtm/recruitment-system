package fu.se.recruitment_system.service.pattern.proxy;

import fu.se.recruitment_system.dto.CreateJobPostRequest;
import fu.se.recruitment_system.dto.JobPostMapper;
import fu.se.recruitment_system.dto.RecruiterJobPostResponse;
import fu.se.recruitment_system.dto.UpdateJobPostRequest;
import fu.se.recruitment_system.model.Category;
import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.JobPostStatus;
import fu.se.recruitment_system.repository.CategoryRepository;
import fu.se.recruitment_system.repository.JobPostRepository;
import fu.se.recruitment_system.repository.UserRepository;
import fu.se.recruitment_system.service.pattern.audit.DraftJobPostAuditLogCreator;
import fu.se.recruitment_system.service.pattern.audit.StatusChangeJobPostAuditLogCreator;
import fu.se.recruitment_system.service.pattern.audit.SubmittedJobPostAuditLogCreator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {
    private final JobPostRepository jobPostRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DraftJobPostAuditLogCreator draftAuditLogCreator;
    private final SubmittedJobPostAuditLogCreator submittedAuditLogCreator;
    private final StatusChangeJobPostAuditLogCreator statusChangeAuditLogCreator;

    @Override
    @Transactional
    public RecruiterJobPostResponse create(Long recruiterId, CreateJobPostRequest request) {
        User recruiter = findUser(recruiterId);
        Category category = findActiveCategory(request.categoryId());
        validateJobData(request.title(), request.jobDescription(), request.requirements(),
                request.applicationDeadline(), request.submitForModeration());

        JobPost jobPost = new JobPost();
        jobPost.setRecruiter(recruiter);
        jobPost.setCategory(category);
        applyCreateRequest(jobPost, request);
        jobPost.setStatus(request.submitForModeration() ? JobPostStatus.PENDING : JobPostStatus.DRAFT);
        JobPost savedJobPost = jobPostRepository.save(jobPost);

        if (savedJobPost.getStatus() == JobPostStatus.DRAFT) {
            draftAuditLogCreator.createAndSave(savedJobPost, recruiter, "Recruiter saved job post as draft");
        } else {
            submittedAuditLogCreator.createAndSave(savedJobPost, recruiter, "Recruiter submitted job post for moderation");
        }
        return JobPostMapper.toRecruiterResponse(savedJobPost);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecruiterJobPostResponse> viewJobPostings(Long recruiterId, JobPostStatus status) {
        List<JobPost> jobPosts = status == null
                ? jobPostRepository.findTop20ByRecruiterIdOrderByCreatedAtDesc(recruiterId)
                : jobPostRepository.findTop20ByRecruiterIdAndStatusOrderByCreatedAtDesc(recruiterId, status);
        return jobPosts.stream().map(JobPostMapper::toRecruiterResponse).toList();
    }

    @Override
    @Transactional
    public RecruiterJobPostResponse edit(Long recruiterId, Long jobPostId, UpdateJobPostRequest request) {
        JobPost jobPost = findRecruiterJob(recruiterId, jobPostId);
        if (jobPost.getStatus() == JobPostStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Closed job posts can only be reactivated");
        }
        validateJobData(request.title(), request.jobDescription(), request.requirements(),
                request.applicationDeadline(), request.submitForModeration());
        jobPost.setCategory(findActiveCategory(request.categoryId()));
        applyUpdateRequest(jobPost, request);
        if (request.submitForModeration()) {
            jobPost.setStatus(JobPostStatus.PENDING);
            jobPost.setRejectionReason(null);
        }
        JobPost savedJobPost = jobPostRepository.save(jobPost);
        statusChangeAuditLogCreator.createAndSave(savedJobPost, savedJobPost.getRecruiter(), "Recruiter edited job post");
        return JobPostMapper.toRecruiterResponse(savedJobPost);
    }

    @Override
    @Transactional
    public RecruiterJobPostResponse close(Long recruiterId, Long jobPostId) {
        JobPost jobPost = findRecruiterJob(recruiterId, jobPostId);
        jobPost.setStatus(JobPostStatus.CLOSED);
        JobPost savedJobPost = jobPostRepository.save(jobPost);
        statusChangeAuditLogCreator.createAndSave(savedJobPost, savedJobPost.getRecruiter(), "Recruiter closed job post");
        return JobPostMapper.toRecruiterResponse(savedJobPost);
    }

    @Override
    @Transactional
    public RecruiterJobPostResponse reactivate(Long recruiterId, Long jobPostId, UpdateJobPostRequest request) {
        JobPost jobPost = findRecruiterJob(recruiterId, jobPostId);
        if (jobPost.getStatus() != JobPostStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only closed job posts can be reactivated");
        }
        validateJobData(request.title(), request.jobDescription(), request.requirements(),
                request.applicationDeadline(), true);
        jobPost.setCategory(findActiveCategory(request.categoryId()));
        applyUpdateRequest(jobPost, request);
        jobPost.setStatus(JobPostStatus.PENDING);
        jobPost.setRejectionReason(null);
        JobPost savedJobPost = jobPostRepository.save(jobPost);
        submittedAuditLogCreator.createAndSave(savedJobPost, savedJobPost.getRecruiter(), "Recruiter reactivated job post for moderation");
        return JobPostMapper.toRecruiterResponse(savedJobPost);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private JobPost findRecruiterJob(Long recruiterId, Long jobPostId) {
        return jobPostRepository.findByIdAndRecruiterId(jobPostId, recruiterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter job post not found"));
    }

    private Category findActiveCategory(Long categoryId) {
        if (categoryId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId is required");
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        if (!category.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inactive categories cannot be selected");
        }
        return category;
    }

    private void validateJobData(
            String title,
            String jobDescription,
            String requirements,
            LocalDateTime applicationDeadline,
            boolean submitted) {
        if (title == null || title.isBlank() || title.length() < 10 || title.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job title must be between 10 and 100 characters");
        }
        if (jobDescription == null || jobDescription.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "jobDescription is required");
        }
        if (submitted && (requirements == null || requirements.isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "requirements is required when submitting");
        }
        if (containsUnsafeContent(jobDescription) || containsUnsafeContent(requirements)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job content contains unauthorized elements");
        }
        if (submitted && (applicationDeadline == null || !applicationDeadline.isAfter(LocalDateTime.now()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "applicationDeadline must be a future date");
        }
    }

    private boolean containsUnsafeContent(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.toLowerCase();
        return normalized.contains("<script") || normalized.contains("javascript:");
    }

    private void applyCreateRequest(JobPost jobPost, CreateJobPostRequest request) {
        jobPost.setTitle(request.title());
        jobPost.setIndustry(request.industry());
        jobPost.setLocation(request.location());
        jobPost.setExperienceLevel(request.experienceLevel());
        jobPost.setWorkType(request.workType());
        jobPost.setJobDescription(request.jobDescription());
        jobPost.setRequirements(request.requirements());
        jobPost.setBenefits(request.benefits());
        jobPost.setSalaryRange(request.salaryRange());
        jobPost.setApplicationDeadline(request.applicationDeadline());
    }

    private void applyUpdateRequest(JobPost jobPost, UpdateJobPostRequest request) {
        jobPost.setTitle(request.title());
        jobPost.setIndustry(request.industry());
        jobPost.setLocation(request.location());
        jobPost.setExperienceLevel(request.experienceLevel());
        jobPost.setWorkType(request.workType());
        jobPost.setJobDescription(request.jobDescription());
        jobPost.setRequirements(request.requirements());
        jobPost.setBenefits(request.benefits());
        jobPost.setSalaryRange(request.salaryRange());
        jobPost.setApplicationDeadline(request.applicationDeadline());
    }
}
