package fu.se.recruitment_system.service.pattern.proxy;

import fu.se.recruitment_system.dto.CreateJobPostRequest;
import fu.se.recruitment_system.dto.RecruiterJobPostResponse;
import fu.se.recruitment_system.dto.UpdateJobPostRequest;
import fu.se.recruitment_system.model.enums.JobPostStatus;

import java.util.List;

public interface JobPostingService {
    RecruiterJobPostResponse create(Long recruiterId, CreateJobPostRequest request);

    List<RecruiterJobPostResponse> viewJobPostings(Long recruiterId, JobPostStatus status);

    RecruiterJobPostResponse edit(Long recruiterId, Long jobPostId, UpdateJobPostRequest request);

    RecruiterJobPostResponse close(Long recruiterId, Long jobPostId);

    RecruiterJobPostResponse reactivate(Long recruiterId, Long jobPostId, UpdateJobPostRequest request);
}
