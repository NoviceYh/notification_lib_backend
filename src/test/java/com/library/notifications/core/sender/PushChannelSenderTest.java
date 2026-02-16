package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.exception.error.DeliveryErrorCode;
import com.library.notifications.api.exception.error.ValidationErrorCode;
import com.library.notifications.api.model.*;
import com.library.notifications.core.port.PushProviderPort;
import com.library.notifications.core.validation.PushValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PushChannelSenderTest {

    @InjectMocks
    private PushChannelSender pushChannelSender;

    @Mock
    private PushProviderPort pushProviderPort;

    @Mock
    private PushValidator pushValidator;

    @Test
    void channel_shouldReturnPush() {
        assertEquals(Channel.PUSH, pushChannelSender.channel());
    }

    @Test
    void send_shouldValidateAndDelegateToProviderPort() {
        List<Recipient> recipients = List.of(new Recipient("testPushRecipient"));
        PushPayload payload = new PushPayload("Subject", "Body");
        NotificationRequest request = new NotificationRequest(Channel.PUSH, recipients, payload);

        Instant now = Instant.now();
        DeliveryResult expected = DeliveryResult.success("provider", "messageId", now);

        doNothing().when(pushValidator).validate(recipients, payload);
        when(pushProviderPort.send(recipients, payload)).thenReturn(expected);

        DeliveryResult result = pushChannelSender.send(request);

        assertSame(expected, result);
        verify(pushValidator).validate(recipients, payload);
        verify(pushProviderPort).send(recipients, payload);
        verifyNoMoreInteractions(pushValidator, pushProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenRequestIsNull() {
        ValidationException ex = assertThrows(ValidationException.class, () -> pushChannelSender.send(null));
        assertEquals(ValidationErrorCode.REQUEST_NULL, ex.getErrorCode());
        verifyNoInteractions(pushValidator, pushProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenChannelIsInvalid() {
        List<Recipient> recipients = List.of(new Recipient("testPushRecipient"));
        PushPayload payload = new PushPayload("Subject", "Body");
        NotificationRequest request = new NotificationRequest(Channel.EMAIL, recipients, payload);

        ValidationException ex = assertThrows(ValidationException.class, () -> pushChannelSender.send(request));
        assertEquals(ValidationErrorCode.INVALID_CHANNEL, ex.getErrorCode());
        verifyNoInteractions(pushValidator, pushProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenPayloadIsEmpty() {
        List<Recipient> recipients = List.of(new Recipient("testPushRecipient"));
        NotificationRequest request = new NotificationRequest(Channel.PUSH, recipients, null);

        ValidationException ex = assertThrows(ValidationException.class, () -> pushChannelSender.send(request));
        assertEquals(ValidationErrorCode.PUSH_PAYLOAD_EMPTY, ex.getErrorCode());
        verifyNoInteractions(pushValidator, pushProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenPayloadIsWrongType() {
        List<Recipient> recipients = List.of(new Recipient("testPushRecipient"));
        NotificationPayload payload = new DummyPayload();
        NotificationRequest request = new NotificationRequest(Channel.PUSH, recipients, payload);

        ValidationException ex = assertThrows(ValidationException.class, () -> pushChannelSender.send(request));
        assertEquals(ValidationErrorCode.PUSH_PAYLOAD_INVALID, ex.getErrorCode());
        verifyNoInteractions(pushValidator, pushProviderPort);
    }

    @Test
    void send_shouldPropagateDeliveryExceptionFromProvider() {
        List<Recipient> recipients = List.of(new Recipient("testPushRecipient"));
        PushPayload payload = new PushPayload("Subject", "Body");
        NotificationRequest request = new NotificationRequest(Channel.PUSH, recipients, payload);

        doNothing().when(pushValidator).validate(recipients, payload);
        when(pushProviderPort.send(recipients, payload))
                .thenThrow(new DeliveryException(DeliveryErrorCode.PROVIDER_TIMEOUT));


        DeliveryException ex = assertThrows(DeliveryException.class, () -> pushChannelSender.send(request));

        assertEquals(DeliveryErrorCode.PROVIDER_TIMEOUT, ex.getErrorCode());
        verify(pushValidator).validate(recipients, payload);
        verify(pushProviderPort).send(recipients, payload);
        verifyNoMoreInteractions(pushValidator, pushProviderPort);
    }

    @Test
    void send_shouldWrapUnexpectedExceptionsFromProvider() {
        List<Recipient> recipients = List.of(new Recipient("testPushRecipient"));
        PushPayload payload = new PushPayload("Subject", "Body");
        NotificationRequest request = new NotificationRequest(Channel.PUSH, recipients, payload);

        doNothing().when(pushValidator).validate(recipients, payload);
        when(pushProviderPort.send(recipients, payload))
                .thenThrow(new RuntimeException("Unexpected error"));

        DeliveryException ex = assertThrows(DeliveryException.class, () -> pushChannelSender.send(request));

        assertEquals(DeliveryErrorCode.UNEXPECTED_ERROR, ex.getErrorCode());
        verify(pushValidator).validate(recipients, payload);
        verify(pushProviderPort).send(recipients, payload);
        verifyNoMoreInteractions(pushValidator, pushProviderPort);
    }

    private record DummyPayload() implements NotificationPayload {}

}