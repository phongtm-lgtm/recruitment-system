package fu.se.recruitment_system.dto;


import fu.se.recruitment_system.model.enums.VerificationStatus;

import java.util.List;

public record CompanyProfileResponse(
        Long id,
        Long userId,
        String companyName,
        String businessField,
        String website,
        String email,
        String phone,
        String address,
        String description,
        String logoUrl,
        String images,
        VerificationStatus verificationStatus,
        List<JobSearchResponse> activeJobs) {
}
