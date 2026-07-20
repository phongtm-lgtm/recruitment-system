package fu.se.recruitment_system.dto;

import fu.se.recruitment_system.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String phone;
    private UserRole role;
    private String department;
    private String permissions;
}
