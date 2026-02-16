package com.library.notifications.core.validation;

import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.exception.error.ValidationErrorCode;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.Recipient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    private EmailValidator emailValidator;

    @BeforeEach
    void setUp() {
        emailValidator = new EmailValidator();
    }

    @Test
    void validate_shouldPass_whenRecipientsAndPayloadAreValid() {
        List<Recipient> recipients = List.of(new Recipient("john.doe@mail.com"));
        EmailPayload payload = new EmailPayload("Hello", "Body");

        assertDoesNotThrow(() -> emailValidator.validate(recipients, payload));
    }

    @Test
    void validate_shouldThrow_whenRecipientsIsNull() {
        EmailPayload payload = new EmailPayload("Hello", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> emailValidator.validate(null, payload));

        assertEquals(ValidationErrorCode.RECIPIENTS_EMPTY, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientsIsEmpty() {
        EmailPayload payload = new EmailPayload("Hello", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> emailValidator.validate(List.of(), payload));

        assertEquals(ValidationErrorCode.RECIPIENTS_EMPTY, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientElementIsNull() {
        List<Recipient> recipients = new ArrayList<>();
        recipients.add(null);

        EmailPayload payload = new EmailPayload("Hello", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> emailValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.EMAIL_NULL_OR_BLANK, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientValueIsNull() {
        List<Recipient> recipients = List.of(new Recipient(null));
        EmailPayload payload = new EmailPayload("Hello", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> emailValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.EMAIL_NULL_OR_BLANK, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenRecipientEmailIsBlankAfterTrim() {
        List<Recipient> recipients = List.of(new Recipient("   "));
        EmailPayload payload = new EmailPayload("Hello", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> emailValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.EMAIL_NULL_OR_BLANK, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenEmailContainsConsecutiveDots() {
        List<Recipient> recipients = List.of(new Recipient("john..doe@mail.com"));
        EmailPayload payload = new EmailPayload("Hello", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> emailValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.EMAIL_CONSECUTIVE_DOTS, ex.getErrorCode());
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("john..doe@mail.com"));
    }

    @Test
    void validate_shouldThrow_whenEmailHasInvalidFormat() {
        List<Recipient> recipients = List.of(new Recipient("not-an-email"));
        EmailPayload payload = new EmailPayload("Hello", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> emailValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.EMAIL_INVALID_FORMAT, ex.getErrorCode());
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("not-an-email"));
    }

    @Test
    void validate_shouldThrow_whenPayloadIsNull() {
        List<Recipient> recipients = List.of(new Recipient("john.doe@mail.com"));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> emailValidator.validate(recipients, null));

        assertEquals(ValidationErrorCode.EMAIL_PAYLOAD_INVALID, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenSubjectIsBlank() {
        List<Recipient> recipients = List.of(new Recipient("john.doe@mail.com"));
        EmailPayload payload = new EmailPayload("   ", "Body");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> emailValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.SUBJECT_EMPTY, ex.getErrorCode());
    }

    @Test
    void validate_shouldThrow_whenBodyIsBlank() {
        List<Recipient> recipients = List.of(new Recipient("john.doe@mail.com"));
        EmailPayload payload = new EmailPayload("Hello", "   ");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> emailValidator.validate(recipients, payload));

        assertEquals(ValidationErrorCode.BODY_EMPTY, ex.getErrorCode());
    }

    @Test
    void validate_shouldAcceptEmailWithSpacesAround_itTrimsBeforeValidation() {
        List<Recipient> recipients = List.of(new Recipient("   john.doe@mail.com   "));
        EmailPayload payload = new EmailPayload("Hello", "Body");

        assertDoesNotThrow(() -> emailValidator.validate(recipients, payload));
    }
}