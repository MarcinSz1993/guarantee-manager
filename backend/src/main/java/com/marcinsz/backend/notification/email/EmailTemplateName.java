package com.marcinsz.backend.notification.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate-account"),
    GUARANTEE_EXPIRES("guarantee-expires"),
    RESET_PASSWORD("reset-password");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}