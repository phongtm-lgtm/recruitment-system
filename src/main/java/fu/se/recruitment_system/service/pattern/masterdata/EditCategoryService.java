package fu.se.recruitment_system.service.pattern.masterdata;

import fu.se.recruitment_system.dto.CategoryRequest;
import fu.se.recruitment_system.model.Category;
import fu.se.recruitment_system.repository.CategoryRepository;
import fu.se.recruitment_system.service.AdminAuthorizationService;
import org.springframework.stereotype.Service;

@Service
public class EditCategoryService extends AbstractMasterDataService {
    public EditCategoryService(
            CategoryRepository categoryRepository,
            AdminAuthorizationService adminAuthorizationService) {
        super(categoryRepository, adminAuthorizationService);
    }

    @Override
    protected Category resolveCategory(Long categoryId, CategoryRequest request) {
        return findCategory(categoryId);
    }

    @Override
    protected void applyOperation(Category category, CategoryRequest request) {
        category.setName(request.name().trim());
        category.setType(resolveType(request.type()));
    }
}
