package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.*;
import com.library.notifications.core.port.PushProviderPort;
import com.library.notifications.core.validation.PushValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.UNEXPECTED_ERROR;
import static com.library.notifications.api.exception.error.ValidationErrorCode.*;

public class PushChannelSender implements ChannelSender {

    private static final Logger logger = LoggerFactory.getLogger(PushChannelSender.class);
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
            String value = request.channel() == null ? "null" : request.channel().name();
            throw new ValidationException(INVALID_CHANNEL, Channel.PUSH.name(), value);
        }

        PushPayload payload = extractPushPayload(request);
        validator.validate(request.recipients(), payload);

        try {
            logger.debug("Sending push notification: recipientsCount={}, titleLength={}, messageLength={}",
                    request.recipients().size(),
                    payload.title() == null ? 0 : payload.title().length(),
                    payload.message() == null ? 0 : payload.message().length());
            return pushProvider.send(request.recipients(), payload);
        } catch (DeliveryException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error sending Push", e);
            throw new DeliveryException(UNEXPECTED_ERROR, e);
        }
    }

    private PushPayload extractPushPayload(NotificationRequest request) {
        if (request.payload() == null) {
            throw new ValidationException(PUSH_PAYLOAD_EMPTY);
        }
        if (!(request.payload() instanceof PushPayload pushPayload)) {
            throw new ValidationException(PUSH_PAYLOAD_INVALID, request.payload().getClass().getSimpleName());
        }
        return pushPayload;
    }
}
