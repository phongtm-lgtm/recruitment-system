package fu.se.recruitment_system.service.pattern.facade;

import fu.se.recruitment_system.dto.CompanyProfileResponse;
import fu.se.recruitment_system.dto.JobDetailResponse;
import fu.se.recruitment_system.dto.JobPostMapper;
import fu.se.recruitment_system.dto.JobSearchResponse;
import fu.se.recruitment_system.model.CompanyProfile;
import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.service.ApplicationHistoryService;
import fu.se.recruitment_system.service.CompanyProfileQueryService;
import fu.se.recruitment_system.service.JobPostingQueryService;
import fu.se.recruitment_system.service.ViewCountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class JobDiscoveryFacadeService implements JobDiscoveryFacade {
    //sub system in facade
    private final JobPostingQueryService jobPostingQueryService;
    private final ApplicationHistoryService applicationHistoryService;
    private final ViewCountService viewCountService;
    private final CompanyProfileQueryService companyProfileQueryService;

    @Override
    @Transactional(readOnly = true)
    public List<JobSearchResponse> searchJobs(String keyword, Long categoryId, String industry, String location) {
        return jobPostingQueryService.searchActiveJobs(keyword, categoryId, industry, location)
                .stream()
                .map(jobPost -> JobPostMapper.toSearchResponse(
                        jobPost,
                        companyProfileQueryService.findCompanyByRecruiter(jobPost)))
                .toList();
    }

    @Override
    @Transactional
    public JobDetailResponse getJobDetail(Long jobPostId, Long jobSeekerId) {
        JobPost viewedJob = viewCountService.incrementViewCount(jobPostingQueryService.getActiveJobDetail(jobPostId));
        long applicationCount = applicationHistoryService.countApplications(viewedJob.getId());
        boolean appliedInLast30Days = applicationHistoryService.hasAppliedInLast30Days(viewedJob.getId(), jobSeekerId);
        return JobPostMapper.toDetailResponse(
                viewedJob,
                companyProfileQueryService.findCompanyByRecruiter(viewedJob),
                applicationCount,
                appliedInLast30Days);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyProfileResponse getCompanyProfile(Long companyProfileId) {
        CompanyProfile companyProfile = companyProfileQueryService.getCompanyProfile(companyProfileId);
        List<JobSearchResponse> activeJobs = jobPostingQueryService.getActiveJobsByCompany(companyProfile)
                .stream()
                .map(jobPost -> JobPostMapper.toSearchResponse(jobPost, companyProfile))
                .toList();
        return new CompanyProfileResponse(
                companyProfile.getId(),
                companyProfile.getUser().getId(),
                companyProfile.getCompanyName(),
                companyProfile.getBusinessField(),
                companyProfile.getWebsite(),
                companyProfile.getEmail(),
                companyProfile.getPhone(),
                companyProfile.getAddress(),
                companyProfile.getDescription(),
                companyProfile.getLogoUrl(),
                companyProfile.getImages(),
                companyProfile.getVerificationStatus(),
                activeJobs);
    }
}
