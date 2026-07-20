package fu.se.recruitment_system.service.pattern.proxy;

import fu.se.recruitment_system.dto.CreateJobPostRequest;
import fu.se.recruitment_system.dto.RecruiterJobPostResponse;
import fu.se.recruitment_system.dto.UpdateJobPostRequest;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.JobPostStatus;
import fu.se.recruitment_system.model.enums.UserRole;
import fu.se.recruitment_system.repository.JobPostRepository;
import fu.se.recruitment_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class JobPostingServiceProxy implements JobPostingService {
    private final JobPostingServiceImpl jobPostingServiceImpl;
    private final UserRepository userRepository;
    private final JobPostRepository jobPostRepository;

    @Override
    public RecruiterJobPostResponse create(Long recruiterId, CreateJobPostRequest request) {
        verifyRecruiter(recruiterId);
        return jobPostingServiceImpl.create(recruiterId, request);
    }

    @Override
    public List<RecruiterJobPostResponse> viewJobPostings(Long recruiterId, JobPostStatus status) {
        verifyRecruiter(recruiterId);
        return jobPostingServiceImpl.viewJobPostings(recruiterId, status);
    }

    @Override
    public RecruiterJobPostResponse edit(Long recruiterId, Long jobPostId, UpdateJobPostRequest request) {
        verifyRecruiterOwnership(recruiterId, jobPostId);
        return jobPostingServiceImpl.edit(recruiterId, jobPostId, request);
    }

    @Override
    public RecruiterJobPostResponse close(Long recruiterId, Long jobPostId) {
        verifyRecruiterOwnership(recruiterId, jobPostId);
        return jobPostingServiceImpl.close(recruiterId, jobPostId);
    }

    @Override
    public RecruiterJobPostResponse reactivate(Long recruiterId, Long jobPostId, UpdateJobPostRequest request) {
        verifyRecruiterOwnership(recruiterId, jobPostId);
        return jobPostingServiceImpl.reactivate(recruiterId, jobPostId, request);
    }

    private void verifyRecruiterOwnership(Long recruiterId, Long jobPostId) {
        verifyRecruiter(recruiterId);
        if (!jobPostRepository.existsByIdAndRecruiterId(jobPostId, recruiterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner recruiter can manage this job post");
        }
    }

    private void verifyRecruiter(Long recruiterId) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter not found"));
        if (recruiter.getRole() != UserRole.RECRUITER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Recruiter permission is required");
        }
    }
}
