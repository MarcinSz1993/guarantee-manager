package com.marcinsz.backend.password;

import com.marcinsz.backend.notification.email.ResetPasswordEmailService;
import com.marcinsz.backend.response.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;
    private final ResetPasswordEmailService resetPasswordEmailService;

    @PostMapping
    public ResponseEntity<ApiResponse> changePassword(Authentication connectedUser,
                                                      @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        passwordService.changePassword(connectedUser, changePasswordRequest);
        return ResponseEntity.accepted().body(ApiResponse.builder()
                .statusCode(HttpStatus.ACCEPTED.value())
                .message("The password have been changed.")
                .build());
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse> sendResetPasswordEmail(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) throws MessagingException {
        resetPasswordEmailService.sendResetPasswordEmail(resetPasswordRequest);
        return ResponseEntity.ok().body(ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("A new password have been sent on your email address.")
                .build());
    }
}
