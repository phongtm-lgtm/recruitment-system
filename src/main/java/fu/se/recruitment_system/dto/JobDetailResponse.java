package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.model.enums.JobPostStatus;

import java.time.LocalDateTime;

public record JobDetailResponse(
        Long id,
        String title,
        String industry,
        String location,
        String experienceLevel,
        String workType,
        String categoryName,
        Long companyProfileId,
        String companyName,
        String jobDescription,
        String requirements,
        String benefits,
        String salaryRange,
        LocalDateTime applicationDeadline,
        boolean applicationClosed,
        JobPostStatus status,
        boolean featured,
        long viewCount,
        long applicationCount,
        boolean appliedInLast30Days,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
