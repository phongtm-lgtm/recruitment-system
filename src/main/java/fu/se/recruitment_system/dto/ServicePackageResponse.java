package fu.se.recruitment_system.dto;

import java.math.BigDecimal;

public record ServicePackageResponse(
        Long id,
        String packageName,
        BigDecimal price,
        int durationDays,
        int quota) {
}
