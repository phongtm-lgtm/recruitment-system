package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.model.enums.JobPostStatus;

import java.time.LocalDateTime;

public record RecruiterJobPostResponse(
        Long id,
        String title,
        String industry,
        String location,
        String categoryName,
        String salaryRange,
        LocalDateTime applicationDeadline,
        JobPostStatus status,
        String rejectionReason,
        boolean featured,
        long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
