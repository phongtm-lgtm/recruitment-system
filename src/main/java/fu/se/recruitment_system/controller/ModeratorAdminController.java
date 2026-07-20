package fu.se.recruitment_system.controller;

import fu.se.recruitment_system.dto.ApiResponse;
import fu.se.recruitment_system.dto.ModeratorResponse;
import fu.se.recruitment_system.dto.ModeratorUpdateRequest;
import fu.se.recruitment_system.dto.RegisterRequest;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.UserStatus;
import fu.se.recruitment_system.service.ModeratorAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/moderators")
@RequiredArgsConstructor
public class ModeratorAdminController {

    private final ModeratorAdminService moderatorAdminService;

    // UC-08: View and Search Moderators
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ModeratorResponse>>> searchModerators(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<User> moderators = moderatorAdminService.searchModerators(keyword, status, department, page, size);
        Page<ModeratorResponse> response = moderators.map(ModeratorResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.success("Moderators retrieved successfully", response));
    }

    // UC-09: Create Moderator Account
    @PostMapping
    public ResponseEntity<ApiResponse<ModeratorResponse>> createModerator(@RequestBody RegisterRequest request) {
        User createdModerator = moderatorAdminService.createModerator(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Moderator account created successfully. Temporary credentials sent to employee.", 
                ModeratorResponse.fromEntity(createdModerator)));
    }

    // UC-10: Edit Moderator Account
    @PutMapping("/{moderatorId}")
    public ResponseEntity<ApiResponse<ModeratorResponse>> editModerator(
            @PathVariable Long moderatorId,
            @RequestBody ModeratorUpdateRequest request) {
        User updated = moderatorAdminService.editModerator(moderatorId, request);
        return ResponseEntity.ok(ApiResponse.success("Moderator account updated successfully", ModeratorResponse.fromEntity(updated)));
    }

    // UC-11: Activate Moderator Account
    @PutMapping("/{moderatorId}/activate")
    public ResponseEntity<ApiResponse<ModeratorResponse>> activateModerator(@PathVariable Long moderatorId) {
        User activated = moderatorAdminService.activateModerator(moderatorId);
        return ResponseEntity.ok(ApiResponse.success("Moderator account activated successfully", ModeratorResponse.fromEntity(activated)));
    }

    // UC-12: Deactivate Moderator Account
    @PutMapping("/{moderatorId}/deactivate")
    public ResponseEntity<ApiResponse<ModeratorResponse>> deactivateModerator(
            @PathVariable Long moderatorId,
            @RequestParam(required = false, defaultValue = "Admin requested deactivation") String reason) {
        User deactivated = moderatorAdminService.deactivateModerator(moderatorId, reason);
        return ResponseEntity.ok(ApiResponse.success(
                "Moderator account deactivated and pending tasks reassigned to general pool.", 
                ModeratorResponse.fromEntity(deactivated)));
    }
}
