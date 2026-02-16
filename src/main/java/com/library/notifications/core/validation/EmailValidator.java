package com.library.notifications.core.validation;

import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.Recipient;

import java.util.List;
import java.util.regex.Pattern;

import static com.library.notifications.api.exception.error.ValidationErrorCode.*;

public class EmailValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public void validate(List<Recipient> recipients, EmailPayload payload) {
        validateRecipients(recipients);
        payloadValidation(payload);
    }

    private void validateRecipients(List<Recipient> recipients) {

        if (recipients == null || recipients.isEmpty()) {
            throw new ValidationException(RECIPIENTS_EMPTY);
        }

        for (Recipient recipient : recipients) {
            if (recipient == null || recipient.value() == null) {
                throw new ValidationException(EMAIL_NULL_OR_BLANK);
            }
            String email = recipient.value().trim();
            if (email.isBlank()) {
                throw new ValidationException(EMAIL_NULL_OR_BLANK);
            }
            if (email.contains("..")) {
                throw new ValidationException(EMAIL_CONSECUTIVE_DOTS, email);
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new ValidationException(EMAIL_INVALID_FORMAT, email);
            }
        }
    }

    private void payloadValidation(EmailPayload payload) {
        if (payload == null) {
            throw new ValidationException(EMAIL_PAYLOAD_INVALID);
        }
        if (payload.subject() == null || payload.subject().isBlank()) {
            throw new ValidationException(SUBJECT_EMPTY);
        }
        if (payload.body() == null || payload.body().isBlank()) {
            throw new ValidationException(BODY_EMPTY);
        }
    }

}
