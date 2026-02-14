package com.library.notifications.api.exception.error;

public enum ValidationErrorCode {

    RECIPIENTS_EMPTY("EV-0001", "The list of recipients cannot be empty"),
    EMAIL_NULL_OR_BLANK("EV-0002", "Email cannot be null or blank"),
    EMAIL_CONSECUTIVE_DOTS("EV-0003", "Email contains consecutive dots"),
    EMAIL_INVALID_FORMAT("EV-0004", "Email has an invalid format"),
    SUBJECT_EMPTY("EV-0005", "The email subject cannot be empty"),
    BODY_EMPTY("EV-0006", "The email body cannot be empty"),
    EMAIL_PAYLOAD_INVALID("EV-0007", "The email payload cannot be null"),
    REQUEST_NULL("EV-0008", "The notification request cannot be null"),
    INVALID_CHANNEL("EV-0009", "Invalid channel");

    private final String code;
    private final String description;

    ValidationErrorCode(String code, String description) {
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
