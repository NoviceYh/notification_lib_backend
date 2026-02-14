package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.Channel;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.NotificationRequest;
import com.library.notifications.core.port.EmailProviderPort;
import com.library.notifications.core.validation.EmailValidator;

import java.util.Objects;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.UNEXPECTED_ERROR;
import static com.library.notifications.api.exception.error.ValidationErrorCode.*;

public class EmailChannelSender implements ChannelSender {

    private final EmailProviderPort emailProvider;
    private final EmailValidator validator;

    public EmailChannelSender(EmailProviderPort emailProvider, EmailValidator validator) {
        this.emailProvider = Objects.requireNonNull(emailProvider, "emailProvider is required");
        this.validator = Objects.requireNonNull(validator, "validator is required");
    }

    @Override
    public Channel channel() {
        return Channel.EMAIL;
    }

    @Override
    public DeliveryResult send(NotificationRequest request) {
        // 1) Validaciones “comunes” de request (canal, recipients, etc.)
        if (request == null) {
            throw new ValidationException(REQUEST_NULL);
        }
        if (request.channel() != Channel.EMAIL) {
            throw new ValidationException(INVALID_CHANNEL, "Expected channel: EMAIL, but received: " + request.channel());
        }

        // 2) Validación y casteo del payload
        EmailPayload payload = extractEmailPayload(request);

        // 3) Validaciones de negocio (subject/body, emails, etc.)
        validator.validate(request.recipients(), payload);

        // 4) Envío usando el PORT (hexagonal)
        try {
            return emailProvider.send(request.recipients(), payload);
        } catch (DeliveryException e) {
            throw e;
        } catch (Exception e) {
            throw new DeliveryException(UNEXPECTED_ERROR, e);
        }
    }

    private EmailPayload extractEmailPayload(NotificationRequest request) {
        if (request.payload() == null) {
            throw new ValidationException(EMAIL_PAYLOAD_INVALID);
        }
        if (!(request.payload() instanceof EmailPayload emailPayload)) {
            throw new ValidationException(EMAIL_PAYLOAD_INVALID);
        }
        return emailPayload;
    }
}
