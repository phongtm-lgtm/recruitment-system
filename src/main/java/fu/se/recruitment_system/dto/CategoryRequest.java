package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.model.enums.CategoryType;

public record CategoryRequest(String name, CategoryType type) {
}
