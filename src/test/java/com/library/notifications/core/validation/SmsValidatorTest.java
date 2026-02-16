package com.library.notifications.core.validation;

import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.exception.error.ValidationErrorCode;
import com.library.notifications.api.model.SmsPayload;
import com.library.notifications.api.model.Recipient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmsValidatorTest {

    private SmsValidator smsValidator;

    @BeforeEach
    void setUp() {
        smsValidator = new SmsValidator();
    }

    @Test
    void validate_shouldPass_whenRecipientsAndPayloadAreValid() {
        List<Recipient> recipients = List.of(new Recipient("+1234567890"));
        SmsPayload payload = new SmsPayload("Body");

        assertDoesNotThrow(() -> smsValidator.validate(recipients, payload));
    }

    @Test
    void validate_shouldThrow_whenRecipientsIsNull() {
        SmsPayload payload = new SmsPayload("Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> smsValidator.validate(null, payload));

        assertEquals(ValidationErrorCode.RECIPIENTS_EMPTY, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientsIsEmpty() {
        SmsPayload payload = new SmsPayload("Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> smsValidator.validate(List.of(), payload));

        assertEquals(ValidationErrorCode.RECIPIENTS_EMPTY, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientElementIsNull() {
        List<Recipient> recipients = new ArrayList<>();
        recipients.add(null);

        SmsPayload payload = new SmsPayload("Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> smsValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.SMS_RECIPIENT_NULL_OR_BLANK, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientValueIsNull() {
        List<Recipient> recipients = List.of(new Recipient(null));
        SmsPayload payload = new SmsPayload("Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> smsValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.SMS_RECIPIENT_NULL_OR_BLANK, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientSmsIsBlankAfterTrim() {
        List<Recipient> recipients = List.of(new Recipient("   "));
        SmsPayload payload = new SmsPayload("Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> smsValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.SMS_RECIPIENT_NULL_OR_BLANK, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenSmsHasInvalidFormat() {
        List<Recipient> recipients = List.of(new Recipient("not-an-phone-number"));
        SmsPayload payload = new SmsPayload("Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> smsValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.SMS_INVALID_FORMAT, ex.getErrorCode());
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("not-an-phone-number"));
    }

    @Test
    void validate_shouldThrow_whenPayloadIsNull() {
        List<Recipient> recipients = List.of(new Recipient("+1234567890"));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> smsValidator.validate(recipients, null));

        assertEquals(ValidationErrorCode.SMS_PAYLOAD_INVALID, ex.getErrorCode());
    }

    @Test
    void validate_shouldAcceptSmsWithSpacesAround_itTrimsBeforeValidation() {
        List<Recipient> recipients = List.of(new Recipient("   +1234567890   "));
        SmsPayload payload = new SmsPayload("Body");

        assertDoesNotThrow(() -> smsValidator.validate(recipients, payload));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_shouldThrow_whenMessageIsNullOrEmpty(String message) {
        List<Recipient> recipients = List.of(new Recipient("+1234567890"));
        SmsPayload payload = new SmsPayload(message);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> smsValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.SMS_MESSAGE_EMPTY, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenMessageExceedsMaxLength() {
        List<Recipient> recipients = List.of(new Recipient("+1234567890"));
        String longMessage = "A".repeat(161); // 161 characters, exceeding the 160 limit
        SmsPayload payload = new SmsPayload(longMessage);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> smsValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.SMS_MESSAGE_TOO_LONG, ex.getErrorCode());
            assertNotNull(ex.getMessage());
            assertTrue(ex.getMessage().contains("Length: 161"));
    }

}