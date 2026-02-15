package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.*;
import com.library.notifications.core.port.PushProviderPort;
import com.library.notifications.core.validation.PushValidator;

import java.util.Objects;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.UNEXPECTED_ERROR;
import static com.library.notifications.api.exception.error.ValidationErrorCode.*;

public class PushChannelSender implements ChannelSender {

    private final PushProviderPort pushProvider;
    private final PushValidator validator;

    public PushChannelSender(PushProviderPort pushProvider, PushValidator validator) {
        this.pushProvider = Objects.requireNonNull(pushProvider, "pushProvider is required");
        this.validator = Objects.requireNonNull(validator, "validator is required");
    }

    @Override
    public Channel channel() {
        return Channel.PUSH;
    }

    @Override
    public DeliveryResult send(NotificationRequest request) {
        if (request == null) {
            throw new ValidationException(REQUEST_NULL);
        }
        if (request.channel() != Channel.PUSH) {
            throw new ValidationException(INVALID_CHANNEL, "Expected channel: PUSH, but received: " + request.channel());
        }

        PushPayload payload = extractPushPayload(request);
        validator.validate(request.recipients(), payload);

        try {
            return pushProvider.send(request.recipients(), payload);
        } catch (DeliveryException e) {
            throw e;
        } catch (Exception e) {
            throw new DeliveryException(UNEXPECTED_ERROR, e);
        }
    }

    private PushPayload extractPushPayload(NotificationRequest request) {
        if (request.payload() == null) {
            throw new ValidationException(PUSH_PAYLOAD_INVALID);
        }
        if (!(request.payload() instanceof PushPayload pushPayload)) {
            throw new ValidationException(PUSH_PAYLOAD_INVALID,
                    "Expected PushPayload but received: " + request.payload().getClass().getSimpleName());
        }
        return pushPayload;
    }
}
