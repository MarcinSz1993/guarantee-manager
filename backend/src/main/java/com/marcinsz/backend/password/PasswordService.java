package com.marcinsz.backend.password;

import com.marcinsz.backend.exception.InvalidPasswordException;
import com.marcinsz.backend.exception.MissingFieldException;
import com.marcinsz.backend.user.User;
import com.marcinsz.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void changePassword(Authentication connectedUser,
                               ChangePasswordRequest changePasswordRequest){
        User user = (User) connectedUser.getPrincipal();
        validateOldAndNewPassword(changePasswordRequest, user);
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    private void validateOldAndNewPassword(ChangePasswordRequest changePasswordRequest, User user) {
        if (changePasswordRequest.getNewPassword() == null || changePasswordRequest.getOldPassword() == null
        || changePasswordRequest.getConfirmPassword() == null){
            throw new MissingFieldException("Please fill all fields");
        }
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
        {
            throw new InvalidPasswordException("The old password is incorrect");
        } else if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new InvalidPasswordException("The password and confirmation do not match");
        }
    }
}
