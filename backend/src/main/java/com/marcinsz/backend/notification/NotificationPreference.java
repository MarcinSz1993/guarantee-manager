package com.marcinsz.backend.notification;

import lombok.Getter;

@Getter
public enum NotificationPreference {
    EMAIL("It sends an email to user 7 days before guarantee expiration",true),
    DASHBOARD("It shows message on the dashboard 7 days before guarantee expiration",false),
    ALL("All options of reminding are activated",true);

    private final String description;
    private final boolean requiresEmail;

    NotificationPreference(String description, boolean requiresEmail) {
        this.description = description;
        this.requiresEmail = requiresEmail;
    }

    public boolean isEmailNotificationEnabled() {
        return requiresEmail;
    }
}
