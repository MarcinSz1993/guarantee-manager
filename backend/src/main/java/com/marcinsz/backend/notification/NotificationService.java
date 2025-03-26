package com.marcinsz.backend.notification;

import com.marcinsz.backend.notification.email.EmailTemplateName;
import jakarta.mail.MessagingException;

public interface NotificationService {
    void sendNotification(String to,
                          String username,
                          EmailTemplateName emailTemplate,
                          String subject,
                          String message) throws MessagingException;
}
