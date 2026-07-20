package fu.se.recruitment_system.service.specification;

import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.UserRole;
import fu.se.recruitment_system.model.enums.UserStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ModeratorSpecification {

    public static Specification<User> filterModerators(String keyword, UserStatus status, String department) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter role = MODERATOR
            predicates.add(criteriaBuilder.equal(root.get("role"), UserRole.MODERATOR));

            // Keyword filter (matches email or fullName)
            if (keyword != null && !keyword.isBlank()) {
                String searchPattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate emailLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern);
                Predicate nameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), searchPattern);
                predicates.add(criteriaBuilder.or(emailLike, nameLike));
            }

            // Status filter
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Department filter
            if (department != null && !department.isBlank()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("department")), department.trim().toLowerCase()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
