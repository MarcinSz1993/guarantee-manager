package com.marcinsz.backend.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequest {
    @Email(message = "Email pattern must be email@email.com")
    @NotBlank(message = "Email address cannot be empty!")
    private String email;
}
