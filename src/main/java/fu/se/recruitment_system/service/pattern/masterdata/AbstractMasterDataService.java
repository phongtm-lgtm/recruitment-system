package fu.se.recruitment_system.service.pattern.masterdata;

import fu.se.recruitment_system.dto.CategoryMapper;
import fu.se.recruitment_system.dto.CategoryRequest;
import fu.se.recruitment_system.dto.CategoryResponse;
import fu.se.recruitment_system.model.Category;
import fu.se.recruitment_system.model.enums.CategoryType;
import fu.se.recruitment_system.repository.CategoryRepository;
import fu.se.recruitment_system.service.AdminAuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
public abstract class AbstractMasterDataService {
    protected final CategoryRepository categoryRepository;
    private final AdminAuthorizationService adminAuthorizationService;

    @Transactional
    public CategoryResponse execute(Long adminId, Long categoryId, CategoryRequest request) {
        adminAuthorizationService.verifyMasterDataPermission(adminId);
        validateRequest(categoryId, request);
        Category category = resolveCategory(categoryId, request);
        applyOperation(category, request);
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.toResponse(savedCategory);
    }

    protected void validateRequest(Long categoryId, CategoryRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category request is required");
        }
        validateName(request.name());
        CategoryType type = resolveType(request.type());
        validateNameIsUnique(categoryId, request.name().trim(), type);
    }

    protected abstract Category resolveCategory(Long categoryId, CategoryRequest request);

    protected abstract void applyOperation(Category category, CategoryRequest request);

    protected Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    protected CategoryType resolveType(CategoryType type) {
        return type == null ? CategoryType.INDUSTRY : type;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank() || name.trim().length() < 3 || name.trim().length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name must be between 3 and 50 characters");
        }
    }

    private void validateNameIsUnique(Long categoryId, String name, CategoryType type) {
        boolean duplicate = categoryId == null
                ? categoryRepository.existsByNameIgnoreCaseAndType(name, type)
                : categoryRepository.existsByNameIgnoreCaseAndTypeAndIdNot(name, type, categoryId);
        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
        }
    }
}
