package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeratorResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String department;
    private String permissions;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ModeratorResponse fromEntity(User user) {
        return ModeratorResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .permissions(user.getPermissions())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
