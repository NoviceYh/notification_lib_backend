package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.*;
import com.library.notifications.core.port.SmsProviderPort;
import com.library.notifications.core.validation.SmsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.UNEXPECTED_ERROR;
import static com.library.notifications.api.exception.error.ValidationErrorCode.*;

public class SmsChannelSender implements ChannelSender{

    private static final Logger logger = LoggerFactory.getLogger(SmsChannelSender.class);
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
            String value = request.channel() == null ? "null" : request.channel().name();
            throw new ValidationException(INVALID_CHANNEL, Channel.SMS.name(), value);
        }

        SmsPayload payload = extractSmsPayload(request);
        validator.validate(request.recipients(), payload);

        try {
            logger.debug("Sending SMS: recipientsCount={}, messageLength={}",
                    request.recipients().size(),
                    payload.message() == null ? 0 : payload.message().length());
            return smsProvider.send(request.recipients(), payload);
        } catch (DeliveryException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error sending Sms", e);
            throw new DeliveryException(UNEXPECTED_ERROR, e);
        }
    }

    private SmsPayload extractSmsPayload(NotificationRequest request) {
        if (request.payload() == null) {
            throw new ValidationException(SMS_PAYLOAD_EMPTY);
        }
        if (!(request.payload() instanceof SmsPayload smsPayload)) {
            throw new ValidationException(SMS_PAYLOAD_INVALID, request.payload().getClass().getSimpleName());
        }
        return smsPayload;
    }
}
