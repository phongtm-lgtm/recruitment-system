package fu.se.recruitment_system.dto;

import java.time.LocalDateTime;

public record CreateJobPostRequest(
        Long categoryId,
        String title,
        String industry,
        String location,
        String experienceLevel,
        String workType,
        String jobDescription,
        String requirements,
        String benefits,
        String salaryRange,
        LocalDateTime applicationDeadline,
        boolean submitForModeration) {
}
