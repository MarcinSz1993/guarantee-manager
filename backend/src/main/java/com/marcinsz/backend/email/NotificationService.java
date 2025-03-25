package com.marcinsz.backend.email;

import jakarta.mail.MessagingException;

public interface NotificationService {
    void sendNotification(String to,
                          String username,
                          EmailTemplateName emailTemplate,
                          String subject,
                          String message) throws MessagingException;
}
