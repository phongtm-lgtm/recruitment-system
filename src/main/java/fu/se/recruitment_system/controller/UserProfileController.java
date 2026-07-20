package fu.se.recruitment_system.controller;

import fu.se.recruitment_system.dto.ApiResponse;
import fu.se.recruitment_system.dto.UpdateProfileRequest;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // UC-07: Get User Profile
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getProfile(@PathVariable Long userId) {
        User user = userProfileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("User profile fetched successfully", user));
    }

    // UC-07: Edit Personal Profile
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {
        User updatedUser = userProfileService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
    }
}
