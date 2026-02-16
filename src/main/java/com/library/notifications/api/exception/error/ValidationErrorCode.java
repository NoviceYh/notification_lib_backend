package com.library.notifications.api.exception.error;

public enum ValidationErrorCode {

    // === General validation errors ===
    REQUEST_NULL("NVG-0001", "The notification request cannot be null"),
    RECIPIENTS_EMPTY("NVG-0002", "The list of recipients cannot be empty"),
    EMPTY_CHANNEL("NVG-0003", "Channel cannot be empty"),
    INVALID_CHANNEL("NVG-0004", "Invalid channel. Expected channel: %s, but received: %s"),

    // === Configuration validation errors ===
    SENDERS_NULL("NVC-0001", "The list of ChannelSenders cannot be null"),
    NO_SENDERS_CONFIGURED("NVC-0002", "No ChannelSenders configured in the system"),
    SENDER_NOT_CONFIGURED("NVC-0003", "No ChannelSender configured for %s"),
    DUPLICATE_SENDER_FOR_CHANNEL("NVC-0004", "More than one ChannelSender registered for %s"),

    // === Email validation errors ===
    EMAIL_NULL_OR_BLANK("NVE-0001", "Email cannot be null or blank"),
    EMAIL_CONSECUTIVE_DOTS("NVE-0002", "Email contains consecutive dots. Invalid email: %s"),
    EMAIL_INVALID_FORMAT("NVE-0003", "Email has an invalid format. Invalid email: %s"),
    SUBJECT_EMPTY("NVE-0004", "The email subject cannot be empty"),
    BODY_EMPTY("NVE-0005", "The email body cannot be empty"),
    EMAIL_PAYLOAD_INVALID("NVE-0006", "The email payload cannot be null"),

    // === SMS validation errors ===
    SMS_RECIPIENT_NULL_OR_BLANK("NVS-0001", "SMS recipient cannot be null or empty"),
    SMS_INVALID_FORMAT("NVS-0002", "Phone has an invalid format. Invalid SMS recipient: %s"),
    SMS_PAYLOAD_INVALID("NVS-0003", "The SMS payload is invalid or null. Expected type: SmsPayload, but received: %s"),
    SMS_PAYLOAD_EMPTY("NVS-0004", "The SMS payload cannot be empty"),
    SMS_MESSAGE_EMPTY("NVS-0005", "The SMS message cannot be empty"),
    SMS_MESSAGE_TOO_LONG("NVS-0006", "The SMS message exceeds the maximum allowed. Length: %s, max: %s"),

    // === Push validation errors ===
    PUSH_RECIPIENT_NULL_OR_BLANK("NVP-0001", "Push recipient cannot be null or empty"),
    PUSH_PAYLOAD_EMPTY("NVP-0002", "The Push payload cannot be null"),
    PUSH_TITLE_EMPTY("NVP-0003", "The Push title cannot be empty"),
    PUSH_MESSAGE_EMPTY("NVP-0004", "The Push message cannot be empty"),
    PUSH_PAYLOAD_INVALID("NVP-0005", "The Push payload is invalid or null. Expected type: PushPayload, but received: %s");

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

    public String format(Object... args) {
        return String.format(description, args);
    }
}
