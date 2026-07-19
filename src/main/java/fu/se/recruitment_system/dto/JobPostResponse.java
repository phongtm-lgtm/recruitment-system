package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.model.enums.JobPostStatus;

import java.time.LocalDateTime;

public record JobPostResponse(
        Long id,
        String title,
        JobPostStatus status,
        boolean featured,
        LocalDateTime featureExpireAt) {
}
