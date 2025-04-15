package com.marcinsz.backend.notification.email;

import com.marcinsz.backend.exception.UserNotFoundException;
import com.marcinsz.backend.notification.NotificationService;
import com.marcinsz.backend.password.PasswordService;
import com.marcinsz.backend.password.ResetPasswordRequest;
import com.marcinsz.backend.user.User;
import com.marcinsz.backend.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResetPasswordEmailService {

    private final PasswordService passwordService;
    private final NotificationService emailNotificationService;
    private final UserRepository userRepository;

    public void sendResetPasswordEmail(ResetPasswordRequest resetPasswordRequest) throws MessagingException {
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException(resetPasswordRequest.getEmail()));
        String resetPassword = passwordService.resetPassword(resetPasswordRequest);
        log.info("Nowe haslo: {}", resetPassword);
        String message = "You asked for a new password. Your new password is " + resetPassword;
        emailNotificationService.sendNotification(user.getEmail(),user.getUserName(),EmailTemplateName.RESET_PASSWORD
        ,"Your new password",message);
    }
}
