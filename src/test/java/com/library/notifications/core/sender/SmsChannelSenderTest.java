package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.exception.error.DeliveryErrorCode;
import com.library.notifications.api.exception.error.ValidationErrorCode;
import com.library.notifications.api.model.*;
import com.library.notifications.core.port.SmsProviderPort;
import com.library.notifications.core.validation.SmsValidator;
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
class SmsChannelSenderTest {

    @InjectMocks
    SmsChannelSender smsChannelSender;
    
    @Mock
    SmsProviderPort smsProviderPort;
    
    @Mock
    SmsValidator smsValidator;

    @Test
    void channel_shouldReturnSms() {
        assertEquals(Channel.SMS, smsChannelSender.channel());
    }

    @Test
    void send_shouldValidateAndDelegateToProviderPort() {
        List<Recipient> recipients = List.of(new Recipient("+1234567890"));
        SmsPayload payload = new SmsPayload("Body");
        NotificationRequest request = new NotificationRequest(Channel.SMS, recipients, payload);

        Instant now = Instant.now();
        DeliveryResult expected = DeliveryResult.success("provider", "messageId", now);

        doNothing().when(smsValidator).validate(recipients, payload);
        when(smsProviderPort.send(recipients, payload)).thenReturn(expected);

        DeliveryResult result = smsChannelSender.send(request);

        assertSame(expected, result);
        verify(smsValidator).validate(recipients, payload);
        verify(smsProviderPort).send(recipients, payload);
        verifyNoMoreInteractions(smsValidator, smsProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenRequestIsNull() {
        ValidationException ex = assertThrows(ValidationException.class, () -> smsChannelSender.send(null));
        assertEquals(ValidationErrorCode.REQUEST_NULL, ex.getErrorCode());
        verifyNoInteractions(smsValidator, smsProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenChannelIsInvalid() {
        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        SmsPayload payload = new SmsPayload("Body");
        NotificationRequest request = new NotificationRequest(Channel.PUSH, recipients, payload);

        ValidationException ex = assertThrows(ValidationException.class, () -> smsChannelSender.send(request));
        assertEquals(ValidationErrorCode.INVALID_CHANNEL, ex.getErrorCode());
        verifyNoInteractions(smsValidator, smsProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenPayloadIsInvalid() {
        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        NotificationRequest request = new NotificationRequest(Channel.SMS, recipients, null);

        ValidationException ex = assertThrows(ValidationException.class, () -> smsChannelSender.send(request));
        assertEquals(ValidationErrorCode.SMS_PAYLOAD_EMPTY, ex.getErrorCode());
        verifyNoInteractions(smsValidator, smsProviderPort);
    }

    @Test
    void send_shouldThrowValidationException_whenPayloadIsWrongType() {
        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        NotificationPayload payload = new DummyPayload();
        NotificationRequest request = new NotificationRequest(Channel.SMS, recipients, payload);

        ValidationException ex = assertThrows(ValidationException.class, () -> smsChannelSender.send(request));
        assertEquals(ValidationErrorCode.SMS_PAYLOAD_INVALID, ex.getErrorCode());
        verifyNoInteractions(smsValidator, smsProviderPort);
    }

    @Test
    void send_shouldPropagateDeliveryExceptionFromProvider() {
        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        SmsPayload payload = new SmsPayload("Body");
        NotificationRequest request = new NotificationRequest(Channel.SMS, recipients, payload);

        doNothing().when(smsValidator).validate(recipients, payload);
        when(smsProviderPort.send(recipients, payload))
                .thenThrow(new DeliveryException(DeliveryErrorCode.PROVIDER_TIMEOUT));


        DeliveryException ex = assertThrows(DeliveryException.class, () -> smsChannelSender.send(request));

        assertEquals(DeliveryErrorCode.PROVIDER_TIMEOUT, ex.getErrorCode());
        verify(smsValidator).validate(recipients, payload);
        verify(smsProviderPort).send(recipients, payload);
        verifyNoMoreInteractions(smsValidator, smsProviderPort);
    }

    @Test
    void send_shouldWrapUnexpectedExceptionsFromProvider() {
        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        SmsPayload payload = new SmsPayload("Body");
        NotificationRequest request = new NotificationRequest(Channel.SMS, recipients, payload);

        doNothing().when(smsValidator).validate(recipients, payload);
        when(smsProviderPort.send(recipients, payload))
                .thenThrow(new RuntimeException("Unexpected error"));

        DeliveryException ex = assertThrows(DeliveryException.class, () -> smsChannelSender.send(request));

        assertEquals(DeliveryErrorCode.UNEXPECTED_ERROR, ex.getErrorCode());
        verify(smsValidator).validate(recipients, payload);
        verify(smsProviderPort).send(recipients, payload);
        verifyNoMoreInteractions(smsValidator, smsProviderPort);
    }

    private record DummyPayload() implements NotificationPayload {}

}