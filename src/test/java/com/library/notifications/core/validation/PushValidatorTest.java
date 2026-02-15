package com.library.notifications.core.validation;

import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.exception.error.ValidationErrorCode;
import com.library.notifications.api.model.PushPayload;
import com.library.notifications.api.model.Recipient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PushValidatorTest {

    private PushValidator pushValidator;

    @BeforeEach
    void setUp() {
        pushValidator = new PushValidator();
    }

    @Test
    void validate_shouldPass_whenRecipientsAndPayloadAreValid() {
        List<Recipient> recipients = List.of(new Recipient("testPushRecipient"));
        PushPayload payload = new PushPayload("Title", "Body");

        assertDoesNotThrow(() -> pushValidator.validate(recipients, payload));
    }

    @Test
    void validate_shouldThrow_whenRecipientsIsNull() {
        PushPayload payload = new PushPayload("Title", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> pushValidator.validate(null, payload));

        assertEquals(ValidationErrorCode.RECIPIENTS_EMPTY, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientsIsEmpty() {
        PushPayload payload = new PushPayload("Title", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> pushValidator.validate(List.of(), payload));

        assertEquals(ValidationErrorCode.RECIPIENTS_EMPTY, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientElementIsNull() {
        List<Recipient> recipients = new ArrayList<>();
        recipients.add(null);

        PushPayload payload = new PushPayload("Title", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> pushValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.PUSH_RECIPIENT_NULL_OR_BLANK, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientValueIsNull() {
        List<Recipient> recipients = List.of(new Recipient(null));
        PushPayload payload = new PushPayload("Title", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> pushValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.PUSH_RECIPIENT_NULL_OR_BLANK, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientPushIsBlankAfterTrim() {
        List<Recipient> recipients = List.of(new Recipient("   "));
        PushPayload payload = new PushPayload("Title", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> pushValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.PUSH_RECIPIENT_NULL_OR_BLANK, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenPayloadIsNull() {
        List<Recipient> recipients = List.of(new Recipient("testPushRecipient"));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> pushValidator.validate(recipients, null));

        assertEquals(ValidationErrorCode.PUSH_PAYLOAD_INVALID, ex.getErrorCode());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_shouldThrow_whenTitleIsBlank(String title) {
        List<Recipient> recipients = List.of(new Recipient("testPushRecipient"));
        PushPayload payload = new PushPayload(title, "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> pushValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.PUSH_TITLE_EMPTY, ex.getErrorCode());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_shouldThrow_whenMessageIsBlank(String message) {
        List<Recipient> recipients = List.of(new Recipient("testPushRecipient"));
        PushPayload payload = new PushPayload("Title", message);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> pushValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.PUSH_MESSAGE_EMPTY, ex.getErrorCode());
    }

    @Test
    void validate_shouldAcceptPushWithSpacesAround_itTrimsBeforeValidation() {
        List<Recipient> recipients = List.of(new Recipient("   testPushRecipient   "));
        PushPayload payload = new PushPayload("Title", "Body");

        assertDoesNotThrow(() -> pushValidator.validate(recipients, payload));
    }

}