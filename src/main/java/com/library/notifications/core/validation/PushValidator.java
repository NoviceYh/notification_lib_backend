package com.library.notifications.core.validation;

import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.PushPayload;
import com.library.notifications.api.model.Recipient;

import java.util.List;

import static com.library.notifications.api.exception.error.ValidationErrorCode.*;

public class PushValidator {

    public void validate(List<Recipient> recipients, PushPayload payload) {
        validateRecipients(recipients);
        payloadValidation(payload);
    }

    private void validateRecipients(List<Recipient> recipients) {

        if (recipients == null || recipients.isEmpty()) {
            throw new ValidationException(RECIPIENTS_EMPTY);
        }

        for (Recipient recipient : recipients) {
            if (recipient == null || recipient.value() == null) {
                throw new ValidationException(PUSH_RECIPIENT_NULL_OR_BLANK);
            }
            String push = recipient.value().trim();
            if (push.isBlank()) {
                throw new ValidationException(PUSH_RECIPIENT_NULL_OR_BLANK);
            }
        }
    }

    private void payloadValidation(PushPayload payload) {
        if (payload == null) {
            throw new ValidationException(PUSH_PAYLOAD_INVALID);
        }
        if (payload.title() == null || payload.title().isBlank()) {
            throw new ValidationException(PUSH_TITLE_EMPTY);
        }
        if (payload.message() == null || payload.message().isBlank()) {
            throw new ValidationException(PUSH_MESSAGE_EMPTY);
        }
    }

}
