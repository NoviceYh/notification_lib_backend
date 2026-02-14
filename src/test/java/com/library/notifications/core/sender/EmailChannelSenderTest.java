package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.exception.error.DeliveryErrorCode;
import com.library.notifications.api.exception.error.ValidationErrorCode;
import com.library.notifications.api.model.*;
import com.library.notifications.core.port.EmailProviderPort;
import com.library.notifications.core.validation.EmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailChannelSenderTest {

    @InjectMocks
    private EmailChannelSender emailChannelSender;

    @Mock
    private EmailProviderPort emailProviderPort;

    @Mock
    private EmailValidator emailValidator;

    @Test
    void channel_shouldReturnEmail() {
        assertEquals(Channel.EMAIL, emailChannelSender.channel());
    }

    @Test
    void send_shouldValidateAndDelegateToProviderPort() {
        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        EmailPayload payload = new EmailPayload("Subject", "Body");
        NotificationRequest request = new NotificationRequest(Channel.EMAIL, recipients, payload);

        Instant now = Instant.now();
        DeliveryResult expected = DeliveryResult.success("provider", "messageId", now);

        doNothing().when(emailValidator).validate(recipients, payload);
        when(emailProviderPort.send(recipients, payload)).thenReturn(expected);

        DeliveryResult result = emailChannelSender.send(request);

        assertSame(expected, result);
        verify(emailValidator).validate(recipients, payload);
        verify(emailProviderPort).send(recipients, payload);
        verifyNoMoreInteractions(emailValidator, emailProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenRequestIsNull() {
        ValidationException ex = assertThrows(ValidationException.class, () -> emailChannelSender.send(null));
        assertEquals(ValidationErrorCode.REQUEST_NULL, ex.getErrorCode());
        verifyNoInteractions(emailValidator, emailProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenChannelIsInvalid() {
        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        EmailPayload payload = new EmailPayload("Subject", "Body");
        NotificationRequest request = new NotificationRequest(Channel.PUSH, recipients, payload);

        ValidationException ex = assertThrows(ValidationException.class, () -> emailChannelSender.send(request));
        assertEquals(ValidationErrorCode.INVALID_CHANNEL, ex.getErrorCode());
        verifyNoInteractions(emailValidator, emailProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenPayloadIsInvalid() {
        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        NotificationRequest request = new NotificationRequest(Channel.EMAIL, recipients, null);

        ValidationException ex = assertThrows(ValidationException.class, () -> emailChannelSender.send(request));
        assertEquals(ValidationErrorCode.EMAIL_PAYLOAD_INVALID, ex.getErrorCode());
        verifyNoInteractions(emailValidator, emailProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenPayloadIsWrongType() {
        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        NotificationPayload payload = new DummyPayload();
        NotificationRequest request = new NotificationRequest(Channel.EMAIL, recipients, payload);

        ValidationException ex = assertThrows(ValidationException.class, () -> emailChannelSender.send(request));
        assertEquals(ValidationErrorCode.EMAIL_PAYLOAD_INVALID, ex.getErrorCode());
        verifyNoInteractions(emailValidator, emailProviderPort);
    }

    @Test
    void send_shouldPropagateDeliveryExceptionFromProvider() {
        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        EmailPayload payload = new EmailPayload("Subject", "Body");
        NotificationRequest request = new NotificationRequest(Channel.EMAIL, recipients, payload);

        doNothing().when(emailValidator).validate(recipients, payload);
        when(emailProviderPort.send(recipients, payload))
                .thenThrow(new DeliveryException(DeliveryErrorCode.PROVIDER_TIMEOUT));


        DeliveryException ex = assertThrows(DeliveryException.class, () -> emailChannelSender.send(request));

        assertEquals(DeliveryErrorCode.PROVIDER_TIMEOUT, ex.getErrorCode());
        verify(emailValidator).validate(recipients, payload);
        verify(emailProviderPort).send(recipients, payload);
        verifyNoMoreInteractions(emailValidator, emailProviderPort);
    }

    @Test
    void send_shouldWrapUnexpectedExceptionsFromProvider() {
        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        EmailPayload payload = new EmailPayload("Subject", "Body");
        NotificationRequest request = new NotificationRequest(Channel.EMAIL, recipients, payload);

        doNothing().when(emailValidator).validate(recipients, payload);
        when(emailProviderPort.send(recipients, payload))
                .thenThrow(new RuntimeException("Unexpected error"));

        DeliveryException ex = assertThrows(DeliveryException.class, () -> emailChannelSender.send(request));

        assertEquals(DeliveryErrorCode.UNEXPECTED_ERROR, ex.getErrorCode());
        verify(emailValidator).validate(recipients, payload);
        verify(emailProviderPort).send(recipients, payload);
        verifyNoMoreInteractions(emailValidator, emailProviderPort);
    }

    private record DummyPayload() implements NotificationPayload {}

}