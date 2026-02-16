package com.library.notifications.core.validation;

import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.Recipient;
import com.library.notifications.api.model.SmsPayload;

import java.util.List;
import java.util.regex.Pattern;

import static com.library.notifications.api.exception.error.ValidationErrorCode.*;

public class SmsValidator {

    /**
     * SMS recipient validation using E.164 international format.
     *
     * <p>Format: {@code +} followed by 7–15 digits (no leading zero).
     * Examples: {@code +5492944123456}, {@code +14155552671}.
     *
     * <p>Validates format only; does not guarantee number existence or SMS delivery.
     */
    private static final Pattern SMS_PATTERN =
            Pattern.compile("^\\+[1-9]\\d{6,14}$");

    /**
     * Maximum allowed length for SMS message content.
     *
     * <p>Based on the standard single-segment SMS limit (160 characters).
     * Format validation only; encoding and provider-specific rules are not considered.
     */
    private static final int MAX_SMS_LENGTH = 160;

    public void validate(List<Recipient> recipients, SmsPayload payload) {
        validateRecipients(recipients);
        payloadValidation(payload);
    }

    private void validateRecipients(List<Recipient> recipients) {

        if (recipients == null || recipients.isEmpty()) {
            throw new ValidationException(RECIPIENTS_EMPTY);
        }

        for (Recipient recipient : recipients) {
            if (recipient == null || recipient.value() == null) {
                throw new ValidationException(SMS_RECIPIENT_NULL_OR_BLANK);
            }
            String sms = recipient.value().trim();
            if (sms.isBlank()) {
                throw new ValidationException(SMS_RECIPIENT_NULL_OR_BLANK);
            }
            if (!SMS_PATTERN.matcher(sms).matches()) {
                throw new ValidationException(SMS_INVALID_FORMAT, sms);
            }
        }
    }

    private void payloadValidation(SmsPayload payload) {
        if (payload == null) {
            throw new ValidationException(SMS_PAYLOAD_INVALID);
        }
        String message = payload.message();
        if (message == null || message.isBlank()) {
            throw new ValidationException(SMS_MESSAGE_EMPTY);
        }
        if (message.length() > MAX_SMS_LENGTH) {
            throw new ValidationException(SMS_MESSAGE_TOO_LONG, String.valueOf(message.length()), String.valueOf(MAX_SMS_LENGTH));
        }
    }
}
