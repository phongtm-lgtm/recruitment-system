package fu.se.recruitment_system.dto;

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
public class AuthRequest {
    private String provider; // "LOCAL" or "GOOGLE"
    private String email;
    private String password;
    private String googleToken;
    private String googleId;
}
