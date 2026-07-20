package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.model.enums.CategoryType;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
