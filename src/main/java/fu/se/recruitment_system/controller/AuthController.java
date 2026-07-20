package fu.se.recruitment_system.controller;

import fu.se.recruitment_system.dto.ApiResponse;
import fu.se.recruitment_system.dto.AuthRequest;
import fu.se.recruitment_system.dto.AuthResponse;
import fu.se.recruitment_system.dto.ChangePasswordRequest;
import fu.se.recruitment_system.dto.ForgotPasswordRequest;
import fu.se.recruitment_system.dto.OtpVerifyRequest;
import fu.se.recruitment_system.dto.RegisterRequest;
import fu.se.recruitment_system.dto.ResetPasswordRequest;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // UC-01: Register Account
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody RegisterRequest request) {
        User registeredUser = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Registration successful. Please check your email for the OTP code.", registeredUser));
    }

    // UC-01: Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestBody OtpVerifyRequest request) {
        authService.verifyOtp(request.getEmail(), request.getOtpCode());
        return ResponseEntity.ok(ApiResponse.success("Account activated successfully. You can now login."));
    }

    // UC-01 Alt 6.1: Resend OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@RequestParam String email) {
        authService.resendOtp(email);
        return ResponseEntity.ok(ApiResponse.success("A new OTP code has been sent to your email."));
    }

    // UC-02 & UC-03: Login (Username/Password or Google Auth Strategy)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    // UC-04: Recover Password (Request Reset Link)
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String msg = authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(msg));
    }

    // UC-04: Reset Password Execution
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok(ApiResponse.success("Password has been reset successfully."));
    }

    // UC-05: Logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) 
                ? authHeader.substring(7) : authHeader;
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully."));
    }

    // UC-06: Change Password
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam Long userId,
            @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully."));
    }
}
