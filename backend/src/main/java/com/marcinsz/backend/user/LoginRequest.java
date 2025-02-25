package com.marcinsz.backend.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Email(message = "Incorrect email format.")
    @NotEmpty
    @NotBlank(message = "Email cannot be empty.")
    private String userEmail;

    @Length(message = "Password should have between 6 and 15 characters.")
    @NotEmpty
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
