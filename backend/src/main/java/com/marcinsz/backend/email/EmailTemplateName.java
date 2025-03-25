package com.marcinsz.backend.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate-account"),
    GUARANTEE_EXPIRES("guarantee-expires");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}