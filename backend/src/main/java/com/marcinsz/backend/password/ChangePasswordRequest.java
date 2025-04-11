package com.marcinsz.backend.password;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotEmpty(message = "Old password field cannot be empty!")
    private String oldPassword;
    @NotEmpty(message = "New password field cannot be empty!")
    private String newPassword;
    @NotEmpty(message = "Confirm password field cannot be empty!")
    private String confirmPassword;
}
