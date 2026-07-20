package fu.se.recruitment_system.service;

import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.enums.UserRole;
import fu.se.recruitment_system.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminAuthorizationService {
    private final UserRepository userRepository;

    public AdminAuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void verifySystemAdmin(Long adminId) {
        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found"));
        if (user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "System admin permission is required");
        }
    }

    public void verifyPackageManagementPermission(Long adminId) {
        verifySystemAdmin(adminId);
    }

    public void verifyAnalyticsPermission(Long adminId) {
        verifySystemAdmin(adminId);
    }

    public void verifyMasterDataPermission(Long adminId) {
        verifySystemAdmin(adminId);
    }
}
