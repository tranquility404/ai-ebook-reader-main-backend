package com.tranquility.ebookreader.model;

public enum Roles {
    ADMIN,
    Moderator,
    USER;

    public String getAuthority() {
        return this.name();
    }
}