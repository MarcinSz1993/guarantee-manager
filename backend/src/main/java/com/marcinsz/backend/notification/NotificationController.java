package com.marcinsz.backend.notification;

import com.marcinsz.backend.response.ApiResponse;
import com.marcinsz.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse> chooseNotificationPreference(Authentication authentication,
                                                                    @RequestParam NotificationPreference notificationPreference){
        userService.chooseNotificationPreference(authentication,notificationPreference);
        return ResponseEntity.accepted().body(ApiResponse.builder()
                        .statusCode(HttpStatus.ACCEPTED.value())
                        .message("Notification preference chosen to " + notificationPreference.name())
                        .build());
    }
}
