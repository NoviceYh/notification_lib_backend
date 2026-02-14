package com.library.notifications.api.exception.error;

public enum DeliveryErrorCode {

    PROVIDER_TIMEOUT("ED-0001", "The notification provider did not respond within the expected time frame"),
    PROVIDER_AUTH_ERROR("ED-0002", "Authentication with the notification provider failed. Check API keys and credentials"),
    PROVIDER_UNAVAILABLE("ED-0003", "The notification provider is currently unavailable. Please try again later"),
    UNEXPECTED_ERROR("ED-0004", "An unexpected error occurred while delivering the notification");

    private final String code;
    private final String description;

    DeliveryErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }
}
