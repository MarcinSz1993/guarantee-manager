package com.marcinsz.backend.user;

import com.marcinsz.backend.notification.NotificationPreference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long userId;
    private String userName;
    private String userEmail;
    private Role role;
    private boolean isEnabled;
    private NotificationPreference notificationPreference;
}
