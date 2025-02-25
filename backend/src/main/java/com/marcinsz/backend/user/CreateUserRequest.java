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
public class CreateUserRequest {
    @Length(min = 2,max = 15, message = "First name should have between 2 and 15 characters.")
    @NotEmpty(message = "First name cannot be empty.")
    @NotBlank(message = "First name cannot be empty.")
    private String firstName;
    @Length(min = 2,max = 15, message = "Last name should have between 2 and 15 characters.")
    @NotEmpty(message = "Last name cannot be empty.")
    @NotBlank(message = "Last name cannot be empty.")
    private String lastName;
    @Length(min = 5,max = 15, message = "Username should have between 5 and 15 characters.")
    @NotEmpty(message = "Username cannot be empty.")
    @NotBlank(message = "Username cannot be empty.")
    private String username;
    @NotEmpty(message = "Password cannot be empty.")
    @Length(min = 5,max = 15, message = "Password should have between 5 and 15 characters.")
    @NotBlank(message = "Password cannot be empty.")
    private String password;
    @Email(message = "Email must be in format: email@email.com")
    @NotEmpty(message = "Email cannot be empty.")
    @NotBlank(message = "Email cannot be empty.")
    private String email;
}
