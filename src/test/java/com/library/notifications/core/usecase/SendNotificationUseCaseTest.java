package com.library.notifications.core.usecase;

import com.library.notifications.api.NotificationService;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.*;
import com.library.notifications.core.sender.ChannelSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static com.library.notifications.api.exception.error.ValidationErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendNotificationUseCaseTest {

    @Test
    void constructor_shouldThrowValidationException_whenSendersIsNull() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> new SendNotificationUseCase(null)
        );
        assertEquals(SENDERS_NULL, ex.getErrorCode());
    }

    @Test
    void constructor_shouldThrowValidationException_whenDuplicateChannelSenderRegistered() {
        ChannelSender emailSender1 = mock(ChannelSender.class);
        ChannelSender emailSender2 = mock(ChannelSender.class);

        when(emailSender1.channel()).thenReturn(Channel.EMAIL);
        when(emailSender2.channel()).thenReturn(Channel.EMAIL);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> new SendNotificationUseCase(List.of(emailSender1, emailSender2))
        );

        assertEquals(DUPLICATE_SENDER_FOR_CHANNEL, ex.getErrorCode());
    }

    @Test
    void send_shouldThrowValidationException_whenRequestIsNull() {
        ChannelSender emailSender = mock(ChannelSender.class);
        when(emailSender.channel()).thenReturn(Channel.EMAIL);

        NotificationService useCase = new SendNotificationUseCase(List.of(emailSender));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> useCase.send(null)
        );

        assertEquals(REQUEST_NULL, ex.getErrorCode());
    }

    @Test
    void send_shouldThrowValidationException_whenChannelIsNull() {
        ChannelSender emailSender = mock(ChannelSender.class);
        when(emailSender.channel()).thenReturn(Channel.EMAIL);

        NotificationService useCase = new SendNotificationUseCase(List.of(emailSender));

        NotificationRequest request = new NotificationRequest(
                null,
                List.of(new Recipient("test@test.com")),
                new EmailPayload("Subject", "Body")
        );

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> useCase.send(request)
        );

        assertEquals(EMPTY_CHANNEL, ex.getErrorCode());
    }

    @Test
    void send_shouldThrowValidationException_whenNoSenderConfiguredForChannel() {
        ChannelSender emailSender = mock(ChannelSender.class);
        when(emailSender.channel()).thenReturn(Channel.EMAIL);

        NotificationService useCase = new SendNotificationUseCase(List.of(emailSender));

        Channel notConfigured = java.util.Arrays.stream(Channel.values())
                .filter(c -> c != Channel.EMAIL)
                .findFirst()
                .orElseThrow();

        NotificationRequest dummyRequest = new NotificationRequest(
                notConfigured,
                List.of(new Recipient("+5491112345678")),
                new DummyPayload("Hello")
        );

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> useCase.send(dummyRequest)
        );

        assertEquals(SENDER_NOT_CONFIGURED, ex.getErrorCode());
    }

    @Test
    void send_shouldDelegateToCorrectChannelSender() {
        ChannelSender emailSender = mock(ChannelSender.class);
        when(emailSender.channel()).thenReturn(Channel.EMAIL);

        NotificationService useCase = new SendNotificationUseCase(List.of(emailSender));

        List<Recipient> recipients = List.of(new Recipient("test@test.com"));
        EmailPayload payload = new EmailPayload("Subject", "Body");
        NotificationRequest request = new NotificationRequest(Channel.EMAIL, recipients, payload);

        Instant now = Instant.now();
        DeliveryResult expected = DeliveryResult.success("provider", "messageId", now);

        when(emailSender.send(request)).thenReturn(expected);

        DeliveryResult result = useCase.send(request);

        assertSame(expected, result);
        verify(emailSender).send(request);
        verifyNoMoreInteractions(emailSender);
    }

    @Test
    void sendAsync_shouldCompleteWithSameResultAsSend() {
        ChannelSender emailSender = mock(ChannelSender.class);
        when(emailSender.channel()).thenReturn(Channel.EMAIL);

        NotificationService useCase = new SendNotificationUseCase(List.of(emailSender));

        List<Recipient> recipients = List.of(new Recipient("asd"));
        EmailPayload payload = new EmailPayload("Subject", "Body");
        NotificationRequest request = new NotificationRequest(Channel.EMAIL, recipients, payload);

        Instant now = Instant.now();
        DeliveryResult expected = DeliveryResult.success("provider", "messageId", now);
        when(emailSender.send(request)).thenReturn(expected);

        assertDoesNotThrow(() -> {
            DeliveryResult asyncResult = useCase.sendAsync(request).join();
            assertSame(expected, asyncResult);
            verify(emailSender).send(request);
            verifyNoMoreInteractions(emailSender);
        });

    }

    public record DummyPayload(String content) implements NotificationPayload {}
}