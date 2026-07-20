package fu.se.recruitment_system.service.pattern.facade;

import fu.se.recruitment_system.dto.CompanyProfileResponse;
import fu.se.recruitment_system.dto.JobDetailResponse;
import fu.se.recruitment_system.dto.JobSearchResponse;

import java.util.List;

public interface JobDiscoveryFacade {
    List<JobSearchResponse> searchJobs(String keyword, Long categoryId, String industry, String location);

    JobDetailResponse getJobDetail(Long jobPostId, Long jobSeekerId);

    CompanyProfileResponse getCompanyProfile(Long companyProfileId);
}
