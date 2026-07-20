package fu.se.recruitment_system.service;

import fu.se.recruitment_system.dto.ModeratorUpdateRequest;
import fu.se.recruitment_system.dto.RegisterRequest;
import fu.se.recruitment_system.event.ModeratorDeactivatedEvent;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.UserRole;
import fu.se.recruitment_system.model.enums.UserStatus;
import fu.se.recruitment_system.repository.UserRepository;
import fu.se.recruitment_system.service.specification.ModeratorSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModeratorAdminService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;
    private final ApplicationEventPublisher eventPublisher;

    // UC-08: View and Search Moderators
    public Page<User> searchModerators(String keyword, UserStatus status, String department, int page, int size) {
        int targetSize = (size <= 0) ? 20 : size; // BR-1: default 20 records per page
        Pageable pageable = PageRequest.of(page, targetSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        return userRepository.findAll(ModeratorSpecification.filterModerators(keyword, status, department), pageable);
    }

    // UC-09: Create Moderator Account
    @Transactional
    public User createModerator(RegisterRequest request) {
        request.setRole(UserRole.MODERATOR);
        return authService.register(request);
    }

    // UC-10: Edit Moderator Account
    @Transactional
    public User editModerator(Long moderatorId, ModeratorUpdateRequest request) {
        User moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new IllegalArgumentException("Moderator account no longer exists."));

        if (moderator.getRole() != UserRole.MODERATOR) {
            throw new IllegalArgumentException("Target account is not a Moderator.");
        }

        if (request.getDepartment() == null || request.getDepartment().isBlank()) {
            throw new IllegalArgumentException("Department is mandatory.");
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            moderator.setFullName(request.getFullName().trim());
        }

        moderator.setDepartment(request.getDepartment().trim());
        moderator.setPermissions(request.getPermissions());

        // BR-1 UC-10: Login email cannot be changed after creation
        return userRepository.save(moderator);
    }

    // UC-11: Activate Moderator Account
    @Transactional
    public User activateModerator(Long moderatorId) {
        User moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new IllegalArgumentException("Moderator not found."));

        if (moderator.getRole() != UserRole.MODERATOR) {
            throw new IllegalArgumentException("Target account is not a Moderator.");
        }

        moderator.setStatus(UserStatus.ACTIVE);
        tokenBlacklistService.clearUserSessionInvalidation(moderatorId);
        return userRepository.save(moderator);
    }

    // UC-12: Deactivate Moderator Account
    @Transactional
    public User deactivateModerator(Long moderatorId, String reason) {
        User moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new IllegalArgumentException("Moderator not found."));

        if (moderator.getRole() != UserRole.MODERATOR) {
            throw new IllegalArgumentException("Target account is not a Moderator.");
        }

        moderator.setStatus(UserStatus.INACTIVE);

        // Invalidate active sessions (UC-12 Step 4)
        tokenBlacklistService.invalidateUserSessions(moderatorId);

        // Publish event to reassign pending moderation tasks to pool (UC-12 Step 5)
        eventPublisher.publishEvent(new ModeratorDeactivatedEvent(this, moderator, reason));

        return userRepository.save(moderator);
    }
}
