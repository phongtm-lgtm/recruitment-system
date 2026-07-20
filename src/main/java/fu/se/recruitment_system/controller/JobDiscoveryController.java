package fu.se.recruitment_system.controller;

import fu.se.recruitment_system.dto.CompanyProfileResponse;
import fu.se.recruitment_system.dto.JobDetailResponse;
import fu.se.recruitment_system.dto.JobSearchResponse;
import fu.se.recruitment_system.service.pattern.facade.JobDiscoveryFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs/discovery")
public class JobDiscoveryController {
    private final JobDiscoveryFacade jobDiscoveryFacade;

    public JobDiscoveryController(JobDiscoveryFacade jobDiscoveryFacade) {
        this.jobDiscoveryFacade = jobDiscoveryFacade;
    }

    @GetMapping
    public List<JobSearchResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String location) {
        return jobDiscoveryFacade.searchJobs(keyword, categoryId, industry, location);
    }

    @GetMapping("/{jobPostId}")
    public JobDetailResponse getJobDetail(
            @PathVariable Long jobPostId,
            @RequestHeader(value = "X-Job-Seeker-Id", required = false) Long jobSeekerId) {
        return jobDiscoveryFacade.getJobDetail(jobPostId, jobSeekerId);
    }

    @GetMapping("/companies/{companyProfileId}")
    public CompanyProfileResponse getCompanyProfile(@PathVariable Long companyProfileId) {
        return jobDiscoveryFacade.getCompanyProfile(companyProfileId);
    }
}
