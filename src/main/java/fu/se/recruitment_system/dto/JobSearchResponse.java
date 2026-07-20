package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.model.enums.JobPostStatus;

import java.time.LocalDateTime;

public record JobSearchResponse(
        Long id,
        String title,
        String industry,
        String location,
        String categoryName,
        String companyName,
        String salaryRange,
        JobPostStatus status,
        boolean featured,
        LocalDateTime createdAt) {
}
