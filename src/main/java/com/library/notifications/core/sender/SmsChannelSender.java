package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.*;
import com.library.notifications.core.port.SmsProviderPort;
import com.library.notifications.core.validation.SmsValidator;

import java.util.Objects;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.UNEXPECTED_ERROR;
import static com.library.notifications.api.exception.error.ValidationErrorCode.*;

public class SmsChannelSender implements ChannelSender{

    private final SmsProviderPort smsProvider;
    private final SmsValidator validator;

    public SmsChannelSender(SmsProviderPort smsProvider, SmsValidator validator) {
        this.smsProvider = Objects.requireNonNull(smsProvider, "smsProvider is required");
        this.validator = Objects.requireNonNull(validator, "validator is required");
    }

    @Override
    public Channel channel() {
        return Channel.SMS;
    }

    @Override
    public DeliveryResult send(NotificationRequest request) {
        if (request == null) {
            throw new ValidationException(REQUEST_NULL);
        }
        if (request.channel() != Channel.SMS) {
            throw new ValidationException(INVALID_CHANNEL, "Expected channel: SMS, but received: " + request.channel());
        }

        SmsPayload payload = extractSmsPayload(request);
        validator.validate(request.recipients(), payload);

        try {
            return smsProvider.send(request.recipients(), payload);
        } catch (DeliveryException e) {
            throw e;
        } catch (Exception e) {
            throw new DeliveryException(UNEXPECTED_ERROR, e);
        }
    }

    private SmsPayload extractSmsPayload(NotificationRequest request) {
        if (request.payload() == null) {
            throw new ValidationException(SMS_PAYLOAD_INVALID);
        }
        if (!(request.payload() instanceof SmsPayload smsPayload)) {
            throw new ValidationException(SMS_PAYLOAD_INVALID,
                    "Expected SmsPayload but received: " + request.payload().getClass().getSimpleName());
        }
        return smsPayload;
    }
}
