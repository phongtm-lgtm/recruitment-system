package fu.se.recruitment_system.service.pattern.masterdata;

import fu.se.recruitment_system.dto.CategoryRequest;
import fu.se.recruitment_system.model.Category;
import fu.se.recruitment_system.repository.CategoryRepository;
import fu.se.recruitment_system.service.AdminAuthorizationService;
import org.springframework.stereotype.Service;

@Service
public class DeactivateCategoryService extends AbstractMasterDataService {
    public DeactivateCategoryService(
            CategoryRepository categoryRepository,
            AdminAuthorizationService adminAuthorizationService) {
        super(categoryRepository, adminAuthorizationService);
    }

    @Override
    protected void validateRequest(Long categoryId, CategoryRequest request) {
        if (categoryId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "categoryId is required");
        }
    }

    @Override
    protected Category resolveCategory(Long categoryId, CategoryRequest request) {
        return findCategory(categoryId);
    }

    @Override
    protected void applyOperation(Category category, CategoryRequest request) {
        category.setActive(false);
    }
}
