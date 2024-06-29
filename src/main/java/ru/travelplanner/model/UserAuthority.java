package ru.travelplanner.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserAuthority implements GrantedAuthority {

    ADMIN,
    MODERATOR,
    USER,
    MODERATOR_FULL;

    @Override
    public String getAuthority() {
        return this.name();
    }
}