package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.Category;
import fu.se.recruitment_system.model.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCaseAndType(String name, CategoryType type);

    boolean existsByNameIgnoreCaseAndTypeAndIdNot(String name, CategoryType type, Long id);

    List<Category> findByActiveTrueOrderByNameAsc();
}
