package com.marcinsz.backend.password;

import com.marcinsz.backend.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;

    @PostMapping
    public ResponseEntity<ApiResponse> changePassword(Authentication connectedUser,
                                                      @RequestBody ChangePasswordRequest changePasswordRequest) {
        passwordService.changePassword(connectedUser, changePasswordRequest);
        return ResponseEntity.accepted().body(ApiResponse.builder()
                .statusCode(HttpStatus.ACCEPTED.value())
                .message("The password have been changed.")
                .build());
    }
}
