package com.marcinsz.backend.user;

import com.marcinsz.backend.notification.NotificationPreference;
import lombok.*;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String userEmail;
    private Role role;
    private boolean isEnabled;
    private NotificationPreference notificationPreference;
}
