package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.model.CompanyProfile;
import fu.se.recruitment_system.model.JobPost;

import java.time.LocalDateTime;

public final class JobPostMapper {
    private JobPostMapper() {
    }

    public static JobSearchResponse toSearchResponse(JobPost jobPost, CompanyProfile companyProfile) {
        return new JobSearchResponse(
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getIndustry(),
                jobPost.getLocation(),
                jobPost.getCategory().getName(),
                companyProfile == null ? null : companyProfile.getCompanyName(),
                jobPost.getSalaryRange(),
                jobPost.getStatus(),
                jobPost.isFeatured(),
                jobPost.getCreatedAt());
    }

    public static RecruiterJobPostResponse toRecruiterResponse(JobPost jobPost) {
        return new RecruiterJobPostResponse(
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getIndustry(),
                jobPost.getLocation(),
                jobPost.getCategory().getName(),
                jobPost.getSalaryRange(),
                jobPost.getApplicationDeadline(),
                jobPost.getStatus(),
                jobPost.getRejectionReason(),
                jobPost.isFeatured(),
                jobPost.getViewCount(),
                jobPost.getCreatedAt(),
                jobPost.getUpdatedAt());
    }

    public static JobDetailResponse toDetailResponse(
            JobPost jobPost,
            CompanyProfile companyProfile,
            long applicationCount,
            boolean appliedInLast30Days) {
        LocalDateTime deadline = jobPost.getApplicationDeadline();
        boolean applicationClosed = deadline != null && deadline.isBefore(LocalDateTime.now());
        return new JobDetailResponse(
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getIndustry(),
                jobPost.getLocation(),
                jobPost.getExperienceLevel(),
                jobPost.getWorkType(),
                jobPost.getCategory().getName(),
                companyProfile == null ? null : companyProfile.getId(),
                companyProfile == null ? null : companyProfile.getCompanyName(),
                jobPost.getJobDescription(),
                jobPost.getRequirements(),
                jobPost.getBenefits(),
                jobPost.getSalaryRange(),
                deadline,
                applicationClosed,
                jobPost.getStatus(),
                jobPost.isFeatured(),
                jobPost.getViewCount(),
                applicationCount,
                appliedInLast30Days,
                jobPost.getCreatedAt(),
                jobPost.getUpdatedAt());
    }
}
