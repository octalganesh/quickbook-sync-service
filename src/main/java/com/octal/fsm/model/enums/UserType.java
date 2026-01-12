package com.octal.fsm.model.enums;

public enum UserType {
    ALL_USER("ALL_USER"),
    PARTICULAR_USER("PARTICULAR_USER"),
    ALL_SUB_ADMIN("ALL_SUB_ADMIN");
    private final String type;

    UserType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

}
