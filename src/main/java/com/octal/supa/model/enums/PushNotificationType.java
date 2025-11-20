package com.octal.supa.model.enums;

public enum PushNotificationType {
    ADMIN_NOTIFICATION("ACTIVE_NOTIFICATION"),
    ;

    private final String status;

    PushNotificationType(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
