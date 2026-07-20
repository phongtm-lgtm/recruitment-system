package fu.se.recruitment_system.controller;

import fu.se.recruitment_system.dto.CategoryRequest;
import fu.se.recruitment_system.dto.CategoryResponse;
import fu.se.recruitment_system.service.pattern.masterdata.CreateCategoryService;
import fu.se.recruitment_system.service.pattern.masterdata.DeactivateCategoryService;
import fu.se.recruitment_system.service.pattern.masterdata.EditCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {
    private final CreateCategoryService createCategoryService;
    private final EditCategoryService editCategoryService;
    private final DeactivateCategoryService deactivateCategoryService;

    public CategoryAdminController(
            CreateCategoryService createCategoryService,
            EditCategoryService editCategoryService,
            DeactivateCategoryService deactivateCategoryService) {
        this.createCategoryService = createCategoryService;
        this.editCategoryService = editCategoryService;
        this.deactivateCategoryService = deactivateCategoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(
            @RequestHeader("X-Admin-Id") Long adminId,
            @RequestBody CategoryRequest request) {
        return createCategoryService.execute(adminId, null, request);
    }

    @PutMapping("/{categoryId}")
    public CategoryResponse edit(
            @RequestHeader("X-Admin-Id") Long adminId,
            @PathVariable Long categoryId,
            @RequestBody CategoryRequest request) {
        return editCategoryService.execute(adminId, categoryId, request);
    }

    @DeleteMapping("/{categoryId}")
    public CategoryResponse deactivate(
            @RequestHeader("X-Admin-Id") Long adminId,
            @PathVariable Long categoryId) {
        return deactivateCategoryService.execute(adminId, categoryId, null);
    }
}
