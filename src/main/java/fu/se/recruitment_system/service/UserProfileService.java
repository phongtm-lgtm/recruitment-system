package fu.se.recruitment_system.service;

import fu.se.recruitment_system.dto.UpdateProfileRequest;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            // Check unique phone if modified
            if (!request.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
                throw new IllegalArgumentException("Phone number is already associated with another account.");
            }
            user.setPhone(request.getPhone().trim());
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl().trim());
        }

        // BR-1 UC-07: Login email cannot be updated here
        return userRepository.save(user);
    }

    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
