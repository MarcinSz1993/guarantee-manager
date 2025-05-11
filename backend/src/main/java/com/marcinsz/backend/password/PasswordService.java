package com.marcinsz.backend.password;

import com.marcinsz.backend.exception.InvalidPasswordException;
import com.marcinsz.backend.exception.MissingFieldException;
import com.marcinsz.backend.exception.UserNotFoundException;
import com.marcinsz.backend.kafka.ResetPasswordKafkaProducer;
import com.marcinsz.backend.response.ApiResponse;
import com.marcinsz.backend.user.User;
import com.marcinsz.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ResetPasswordKafkaProducer resetPasswordKafkaProducer;
    
    public String resetPassword(ResetPasswordRequest resetPasswordRequest){
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> UserNotFoundException.byEmail(resetPasswordRequest.getEmail()));
        String generatedNewPassword = generateNewPassword();
        user.setPassword(passwordEncoder.encode(generatedNewPassword));
        userRepository.save(user);
        resetPasswordKafkaProducer.sendMessage(
                ApiResponse.builder()
                        .message("User " + user.getFirstName() + " "+ user.getLastName() + " has been reset the password.")
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
        return generatedNewPassword;
    }

    public void changePassword(Authentication connectedUser,
                               ChangePasswordRequest changePasswordRequest){
        User user = (User) connectedUser.getPrincipal();
        validateOldAndNewPassword(changePasswordRequest, user);
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    private String generateNewPassword(){
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();
        List<Character> passwordChars = new ArrayList<>();

        String specialChars = "!@#$%^&*()_+";
        String numbers = "0123456789";
        String bigLetters = "ABCDEFGHIJKLMNOUPRSTWXYZ";
        String smallLetters = "abcdefghijklmnouprstwxyz";
        String allAllowedCharacters = specialChars + numbers + bigLetters + smallLetters;

        int randomNumberForSpecialChars = random.nextInt(specialChars.length());
        int randomNumberForBigLetters = random.nextInt(bigLetters.length());
        int randomNumberForSmallLetters = random.nextInt(smallLetters.length());
        int randomNumberForNumbers = random.nextInt(numbers.length());

        char number = numbers.charAt(randomNumberForNumbers);
        char smallLetter = smallLetters.charAt(randomNumberForSmallLetters);
        char specialChar = specialChars.charAt(randomNumberForSpecialChars);
        char bigLetter = bigLetters.charAt(randomNumberForBigLetters);

        passwordChars.add(specialChar);
        passwordChars.add(bigLetter);
        passwordChars.add(number);
        passwordChars.add(smallLetter);

        for (int i = 0; i < 4; i++) {
            int randomNumber = random.nextInt(allAllowedCharacters.length());
            char character = allAllowedCharacters.charAt(randomNumber);
            passwordChars.add(character);
        }
        Collections.shuffle(passwordChars,random);
        for (Character passwordChar : passwordChars) {
            password.append(passwordChar);
        }
        return password.toString();
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
