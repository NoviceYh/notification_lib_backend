package com.library.notifications.config;

import com.library.notifications.api.NotificationService;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.exception.error.ValidationErrorCode;
import com.library.notifications.api.model.*;
import com.library.notifications.core.validation.EmailValidator;
import com.library.notifications.core.validation.PushValidator;
import com.library.notifications.core.validation.SmsValidator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class NotificationClientBuilderTest {

    @Test
    void build_shouldThrowValidationException_whenNoSendersConfigured() {
        NotificationClientBuilder builder = NotificationClientBuilder.builder();

        ValidationException ex = assertThrows(ValidationException.class, builder::build);
        assertEquals(ValidationErrorCode.NO_SENDERS_CONFIGURED, ex.getErrorCode());
    }

    @Test
    void build_shouldBuildService_whenEmailProviderConfigured() {
        NotificationService service = NotificationClientBuilder.builder()
                .useSendGrid("SG_KEY", "no-reply@example.com")
                .build();

        NotificationRequest req = new NotificationRequest(
                Channel.EMAIL,
                List.of(new Recipient("user@example.com")),
                new EmailPayload("Subject", "Body")
        );

        DeliveryResult result = service.send(req);

        assertNotNull(result);
        assertEquals(DeliveryResult.Status.SUCCESS, result.status());
        assertNotNull(result.timestamp());
        assertNotNull(result.provider());
    }

    @Test
    void build_shouldBuildService_whenSmsProviderConfigured() {
        NotificationService service = NotificationClientBuilder.builder()
                .useTwilio("SID", "TOKEN", "+14155552671")
                .build();

        NotificationRequest req = new NotificationRequest(
                Channel.SMS,
                List.of(new Recipient("+5492944123456")),
                new SmsPayload("Hello")
        );

        DeliveryResult result = service.send(req);

        assertNotNull(result);
        assertEquals(DeliveryResult.Status.SUCCESS, result.status());
        assertNotNull(result.timestamp());
        assertNotNull(result.provider());
    }

    @Test
    void build_shouldBuildService_whenPushProviderConfigured() {
        NotificationService service = NotificationClientBuilder.builder()
                .useFirebasePush("project-id", "{serviceAccountJson}")
                .build();

        NotificationRequest req = new NotificationRequest(
                Channel.PUSH,
                List.of(new Recipient("device-token-123")),
                new PushPayload("Title", "Body")
        );

        DeliveryResult result = service.send(req);

        assertNotNull(result);
        assertEquals(DeliveryResult.Status.SUCCESS, result.status());
        assertNotNull(result.timestamp());
        assertNotNull(result.provider());
    }

    @Test
    void build_shouldUseProvidedValidators_whenConfigured() {
        EmailValidator emailValidator = new EmailValidator();
        SmsValidator smsValidator = new SmsValidator();
        PushValidator pushValidator = new PushValidator();

        NotificationService service = NotificationClientBuilder.builder()
                .useSendGrid("SG_KEY", "no-reply@example.com")
                .useTwilio("SID", "TOKEN", "+14155552671")
                .useFirebasePush("project-id", "{serviceAccountJson}")
                .emailValidator(emailValidator)
                .smsValidator(smsValidator)
                .pushValidator(pushValidator)
                .build();

        assertNotNull(service);
    }

    @Test
    void build_shouldReturnServiceWithAsyncSupport_usingCustomExecutor() {
        Executor executor = Executors.newFixedThreadPool(2);

        NotificationService service = NotificationClientBuilder.builder()
                .useSendGrid("SG_KEY", "no-reply@example.com")
                .asyncExecutor(executor)
                .build();

        NotificationRequest req = new NotificationRequest(
                Channel.EMAIL,
                List.of(new Recipient("user@example.com")),
                new EmailPayload("Subject", "Body")
        );

        DeliveryResult result = assertDoesNotThrow(() -> service.sendAsync(req).join());
        assertNotNull(result);
        assertEquals(DeliveryResult.Status.SUCCESS, result.status());
    }

}