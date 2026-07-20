package fu.se.recruitment_system.service;

import fu.se.recruitment_system.model.CompanyProfile;
import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.repository.CompanyProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class CompanyProfileQueryService {
    private final CompanyProfileRepository companyProfileRepository;

    public CompanyProfileQueryService(CompanyProfileRepository companyProfileRepository) {
        this.companyProfileRepository = companyProfileRepository;
    }

    public CompanyProfile getCompanyProfile(Long companyProfileId) {
        return companyProfileRepository.findById(companyProfileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company profile not found"));
    }

    public CompanyProfile findCompanyByRecruiter(JobPost jobPost) {
        return companyProfileRepository.findByUserId(jobPost.getRecruiter().getId()).orElse(null);
    }
}
